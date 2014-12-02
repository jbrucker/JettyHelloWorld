package org.ske.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A Servlet filter for logging request information.
 * In this filter, it prints the requests headers on System.out.
 * @author jim
 */
public class RequestLoggingFilter implements javax.servlet.Filter {
	private static Logger logger;
	private static Marker marker;
	
	/** initialize filter for use */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// normally you use the full class name as logger category.
		// I'm using the simple name to make log messages shorter.
		logger = LoggerFactory.getLogger(this.getClass().getSimpleName() );
		marker = MarkerFactory.getMarker("");
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
					throws IOException, ServletException {
		
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) req;
			logger.info( marker, String.format("From %s:%d  %s %s", 
							request.getRemoteAddr(), request.getRemotePort(), request.getMethod(), request.getRequestURI()
							)
						);
		}
		// pass request to next filter in chain
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {
		// cleanup after context is stopped
	}

}
