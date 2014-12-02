package org.ske;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import org.ske.resource.HelloResource;

/**
 * Application is class defined by JAX-RS for configuring resources
 * in a RESTful application.  Application has 2 methods, getClasses()
 * and getSingletons().  Using an application can be easier than
 * configuration package names or class names in either web.xml or
 * as init parameters to ServletContainer.
 * 
 * Jersey provides ResourceConfig as a subclass of Application with
 * some convenience methods.
 * 
 * In this case, we use a ResourceConfig to tell Jersey that we
 * want it to honor security annotations like @RolesAllowed.
 * For this you must register the RolesAllowedDynamicFeature class
 * with the application.  This is done in the constructor.
 * 
 * @author jim
 *
 */
public class MyApplication extends ResourceConfig {

	
	public MyApplication( ) {
		// easy way to configure singleton resource classes
		super( HelloResource.class );
		// this is necessary if you want to use annotations in resource classes for role-based authentication
		// otherwise, the annotations are ignored
		register( RolesAllowedDynamicFeature.class );
	}

}
