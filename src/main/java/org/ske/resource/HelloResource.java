package org.ske.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/hello")
public class HelloResource {

	@GET
	public Response getHello( ) {
		return Response.ok("Hello Nerd").build();
	}
}
