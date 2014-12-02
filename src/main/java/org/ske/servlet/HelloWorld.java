package org.ske.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
 
/**
 * Example class for sample project using Jetty with Maven.
 *
 * <b>How to Build and Run using Maven</b>
 * 
 * <p>
 * Clean build products: <tt>mvn clean</tt>
 * </p>
 * <p>
 * Compile the project: <tt>mvn compile</tt>
 * </p>
 * <p>
 * Run the project: <tt>mvn exec:java</tt>
 * </p>
 * <p>
 * Create Maven site web pages: <tt>mvn site</tt>
 * </p>
 * <p>
 * Create javadoc in target/site/apidocs: <tt>mvn javadoc:javadoc</tt>
 * </p>
 * How does Maven know what to run and how? Its defined in pom.xml.
 * <p>
 * See what maven is doing:  <tt>mvn dependency:tree</tt>
 * </p> 
 *
 * @see https://wiki.eclipse.org/Jetty/Tutorial/Jetty_and_Maven_HelloWorld
 */
public class HelloWorld extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Hello World</h1>");
    }
}
