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

## Adding Filters to an Embedded Jetty Project

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
holder.setInitParameter("maxRequestsPerSec", "1"); // "1" for testing
holder.setInitParameter("delayMs", "200"); // "-1" to reject excess request
holder.setInitParameter("remotePort", "false"); // "true" may be useful

context.addFilter( holder, "/*", SCOPE );

server.setHandler( context );
```

## Build and Run this Project using Maven

The first time, Maven will download a _lot_ of stuff. 
```
>  mvn clean
>  mvn compile
>  mvn exec:java

[lots of messages]
Jetty Server started on port 8080...
```
you can also write the  goals on one line: `mvn clean compile exec:java`.

Now send some requests to ```http://localhost:8080/hello```.
You should see a log message printed by the RequestLoggingFilter.

If you send requests faster than 1 per second (click "refresh" really fast), you should see some messages like this:
```
INFO RequestLoggingFilter - From 127.0.0.1:51830  GET /hello
WARN org.eclipse.jetty.servlets.DoSFilter - DOS ALERT: Request rejected ip=127.0.0.1,session=null,user=null
```

The "WARN" message shows you the DosFilter is working.  As you can see below, I configured it to allow only 1 request per second per client, and to REJECT excess requests (the normal behavior is to queue extra requests and insert a delay).
Realistically, you would allow more requests per second and not reject excess requests until the request queue is full.

When a request is rejected your browser should display:

**HTTP ERROR: 503**<br>
Service Unavailable

## Request Rate Limiting using DoSFilter

An application may need to limit the rate of requests to prevent a DoS
attack or simply prevent one client from tying up the server at expense
of others.

The Jetty DoSFilter does this, as documented at https://wiki.eclipse.org/Jetty/Reference/DoSFilter.

The DoSFilter uses a priority queue for requests. It gives priority to:
* authenticated requests - use the <tt>extactUserId(ServletRequest)</tt> method to identify authenticated users
* requests with an HttpSession
* connections identified by IP address
* (lowest priority) requests with no way to identify requester

To use this filter, your web application needs these two Jar files:
jetty-util.jar and
jetty-servlets.jar

For a Maven project, include these artifacts in your dependencies:
```
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-server</artifactId>
    <version>${jetty.version}</version>
</dependency>
<dependency>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-servlets</artifactId>
    <version>${jetty.version}</version>
</dependency>
```

## Parameters to Configure DoSFilter

The DoSFilter has many parameters to configure filter behavior and allowed request rates. They are all described in the DoSFilter Javadoc.

Some parameters I used are:

* `maxRequestsPerSec` number of requests per connection per second
* `delayMs` how long to delay requests over the rate limit. -1 means to reject.
* `trackSessions = false` where to track sessions if a Session exists. Default is true.
* `remotePort = true` track sender by IpAddr+port. Default is false.
* `ipWhitelist` comma separated list of IP addresses not rate limited. Use 127.0.0.1 if another local app is using this service.

The Jetty documentation doesn't tell you __how__ to set these parameters in an embedded application (they only describe settings in `web.xml`). 

See the method JettyMain.startServer() for how I did using a FilterHolder. Creating an instance of DoSFilter and calling dosFilter.setXXX(yy) methods didn't work!

## Example of how to write your own rate-limiting filter in Jetty:

If the DoSFilter doesn't do what you want, this example of writing a rate limiting filter may be helpful.

http://alvinalexander.com/java/jwarehouse/jetty-6.1.9/modules/util/src/main/java/org/mortbay/servlet/ThrottlingFilter.java.shtml

## Logging Filter and Simple Logging for Java (slf4j)

This sample code also has a request logging filter.
At first I used java.util.logging.Logger to output messages, but the log messages are ugly
so I switched to Simple Logging for Java (slf4j).
slf4j is a wrapper for other logging frameworks, such as Log4J.

To use slf4j in a Maven project requires dependencies for slf4j-api,
an actual logger (e.g. Log4J), and an slf4j adapter for your logger (slf4j-log4j).
For this project, I used the "simple logging" adapter that just prints messages
on System.out.

* The Maven dependencies for sl4fj are described here: http://java.dzone.com/articles/adding-slf4j-your-maven
* How to use slf4j is described here: http://www.slf4j.org/manual.html


