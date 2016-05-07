package com.cybercom.librarytest.rest;

//import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom application config for the REST services of the library.
 * @author Lennart Moraeus
 */
@ApplicationPath("rest")
public class ApplicationConfig extends Application {

	private final Set<Class<?>> classes;

	public ApplicationConfig() {
		HashSet<Class<?>> c = new HashSet<>();
		c.add(BookRestService.class);
		c.add(AuthorRestService.class);
		//c.add(MOXyJsonProvider.class);

		classes = Collections.unmodifiableSet(c);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
}