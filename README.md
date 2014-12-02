Filter Example Using Jetty and Maven
====================================

This is a simple web service with a single resource (/hello)
that illustrates using a rate-limiting filter and logging
filter with embedded Jetty.

Rate limiting is done using Jetty's DoSFilter.
Request logging is done using a filter class I wrote (its easy)
that implements javax.servlet.Filter.

Both filters use Simple Logging for Java (slf4j), which is
included in the Maven dependencies.

Below is a description of how to configure the DosFilter,
how to run the project, and general method of adding a filter
to an embedded Jetty project.

## Adding Filters in an Embedded Jetty Project

The org.ske.JettyMain class shows how to configure filters
and add them to a context.  After you create a ContextHandler,
you use the ``context.addFilter`` method (which is overloaded).
An example is:
```
// ContextHandler for your web service or servlets:
ServletContextHandler context = new ServletContextHandler();
context.setContextPath("/");
// add servlets
// ...

// add filters
EnumSet<DispatcherType> SCOPE = EnumSet.of(DispatcherType.REQUEST);
// my request logging filter
context.addFilter( RequestLoggingFilter.class, "/*", SCOPE );
// Jetty DoSFilter, wrapped so we can set init parameters
FilterHolder holder = new FilterHolder( DoSFilter.class );
// see DoSFilter Javadoc for names and meanings of init parameters
holder.setInitParameter("maxRequestsPerSec", "5"); // "1" for testing
holder.setInitParameter("delayMs", "200"); // "-1" to reject excess request
holder.setInitParameter("remotePort", "true"); // maybe useful for web service

context.addFilter( holder, "/*", SCOPE );

server.setHandler( context );
```

## How to Build and Run using Maven

The first time, it will download a _lot_ of stuff. 
```
  mvn clean
  mvn compile
  mvn exec:java
```
you can write all the goals on one line: ```mvn clean compile exec:java```.

## Request Rate Limiting

An application may need to limit the rate of requests to prevent a DoS
attack or simply prevent one client from tying up the server at expense
of others.

Jetty has a DoS Filter for this, documented at https://wiki.eclipse.org/Jetty/Reference/DoSFilter.

The Jetty DoS Filter uses a priority queue for requests. It gives priority to:
* authenticated requests - use the <tt>extactUserId(ServletRequest)</tt> method to identify authenticated users
* requests with an HttpSession
* connections identified by IP address
* (lowest priority) requests with no way to identify requester

Your web application needs these 2 Jar files available to the application at run-time:
jetty-util.jar and
jetty-servlets.jar

Maven Dependencies: `jetty-util.jar` is included in the `jetty-server` artifact. `jetty-servets.jar` is artifact `jetty-servlets`.

The DoSFilter has a lot of parameters to configure how the filter behaves, and the rates of requests. They are all listed in the Javadoc.  The default behavior is to set limits based on sessions.  But a RESTful web service doesn't use sessions, so a more suitable way to filter may be like this:

* `maxRequestsPerSec` number of requests per connection per second
* `delayMs` how long to delay requests over the rate limit
* `trackSessions = false` where to track sessions if a Session exists. Default is true.
* `remotePort = true` track sender by IpAddr+port. Default is false.
* `ipWhitelist` comma separated list of IP addresses not rate limited. Use 127.0.0.1 if another local app is using this service.

Problem is, how to set these filter parameters? Jetty documentation describes how to do it in `web.xml`, but for embedded Jetty we're not using web.xml.

* Example of how to write your own rate-limiting filter in Jetty:

http://alvinalexander.com/java/jwarehouse/jetty-6.1.9/modules/util/src/main/java/org/mortbay/servlet/ThrottlingFilter.java.shtml

## Logging Filter and Simple Logging for Java (slf4j)

This sample code also has a request logging filter.
I used the java.util.logging.Logger for that, but the log messages are ugly
so I added Simple Logging for Java (slf4j).
slf4j is a wrapper for another logging facility, such as Log4J (which I use),
Commons Logging, Java Logging, plain System.out, or "noop" logging.

To use slf4j in a Maven project requires dependencies for slf4j-api,
an actual logger (typically Log4J), and an slf4j adapter for your logger (slf4j-log4j).
For this project, I used the "simple logging" adapter that just prints messages
on System.out.

* The Maven dependencies for sl4fj are described here: http://java.dzone.com/articles/adding-slf4j-your-maven
* How to use slf4j is described here: http://www.slf4j.org/manual.html


