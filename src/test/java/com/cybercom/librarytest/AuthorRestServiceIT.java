package com.cybercom.librarytest;

import org.junit.Test;

import com.cybercom.librarytest.model.Author;
import com.cybercom.librarytest.model.Authors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.StringWriter;
import java.net.URI;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests for the Author REST service.
 * @author Lennart Moraeus
 */
public class AuthorRestServiceIT extends RestServiceIntegrationTest {

	private static WebTarget target = client.target(AUTHOR_BASE_URI);

	private static final String XML = 
			  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+   "<author>"
			+     "<name>Douglas Adams</name>"
			+   "</author>";
	private static final String XML_AUTHORS = 
			  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<authors>"
			+   "<author>"
			+     "<name>Douglas Adams</name>"
			+   "</author>"
			+   "<author>"
			+     "<name>Terry Pratchett</name>"
			+   "</author>"
			+   "<author>"
			+     "<name>Neil Gaiman</name>"
			+   "</author>"
			+ "</authors>";
	
	@Test
	public void shouldMarshallAnAuthor() throws JAXBException {
		// given
		Author author = new Author("Douglas Adams");
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(Author.class);
		Marshaller m = context.createMarshaller();
		m.marshal(author, writer);

		// then
		assertEquals(XML, writer.toString());
	}

	@Test
	public void shouldMarshallAListOfAuthors() throws JAXBException {
		Authors books = new Authors();
		books.add(new Author("Douglas Adams"));
		books.add(new Author("Terry Pratchett"));
		books.add(new Author("Neil Gaiman"));
		StringWriter writer = new StringWriter();
		Class<?>[] classes = new Class<?>[2];
		classes[0] = Authors.class;
		classes[1] = Author.class;
		JAXBContext context = JAXBContext.newInstance(classes);
		Marshaller m = context.createMarshaller();
		m.marshal(books, writer);
		assertEquals(XML_AUTHORS, writer.toString());
	}


	@Test
	public void shouldCreateUpdateAndDeleteAnAuthor() throws JAXBException {

		Author author = new Author("Douglas Adams");

		// POSTs (creates) an Author
		Response response = target.request()
				.post(Entity.entity(author, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		URI authorURI = response.getLocation();
		response.close();

		// PUTs (updates) the author
		String authorId = authorURI.toString().split("/")[6];
		Author updatedAuthor = new Author(
				Long.parseLong(authorId), // <- Same id, 
				"Updated author name"     // <- updated name. 
		);
		response = target.request()
				.put(Entity.entity(updatedAuthor, MediaType.APPLICATION_XML));
		assertEquals("OK", response.getStatusInfo().toString());
		response.close();
		
		// GETs the author by location, confirms the updated name
		response = client.target(authorURI).request().get();
		author = response.readEntity(Author.class);
		assertEquals("OK", response.getStatusInfo().toString());
		assertEquals("Updated author name", author.getName());
		response.close();

		// GETs the author id and DELETEs it
		response = target.path(authorId).request().delete();
		assertEquals("No Content", response.getStatusInfo().toString());
		response.close();

		// GETs the author by location and confirms that it has been deleted
		response = client.target(authorURI).request().get();
		assertEquals("Not Found", response.getStatusInfo().toString());
		response.close();
	}

	@Test
	public void shouldNotFindTheAuthorID() throws JAXBException {

		// GETs an author with an unknown ID
		Response response = target.path("invalidID").request().get();
		assertEquals("Not Found", response.getStatusInfo().toString());
		response.close();
	}
}