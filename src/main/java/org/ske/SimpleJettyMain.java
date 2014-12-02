package org.ske;

import org.eclipse.jetty.server.Server;
import org.ske.servlet.HelloWorld;

/**
 * Configure embedded Jetty server with HelloWorld servlet (handler).
 * 
 * @author jim
 *
 */
public class SimpleJettyMain {
	private static final int PORT = 8080;

	/**
	 * Launch an embedded Jetty server on designated port.
	 * @param args first arg is optional port number
	 */
	public static void main(String[] args) throws Exception {
		int port = PORT;
		if (args.length > 0) try {
			port = Integer.parseInt(args[0]);
		} catch ( NumberFormatException nfe ) {
			usage();
		}
		
		Server server = new Server(port);
		server.setHandler(new HelloWorld());

		server.start();
		// wait for server to exit
		server.join();
	}

	private static void usage() {
		System.out.println("usage:  java "+SimpleJettyMain.class.getName()+" [port]");
		System.exit(1);
		
	}
}
