package com.cybercom.librarytest;

import org.junit.Test;

import com.cybercom.librarytest.model.Author;
import com.cybercom.librarytest.model.Book;
import com.cybercom.librarytest.model.Books;

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
 * Integration tests for the Book REST service.
 * @author Lennart Moraeus
 */
public class BookRestServiceIT extends RestServiceIntegrationTest {
	
	private static WebTarget target = client.target(BOOK_BASE_URI);
	
	private static final String XML = 
			  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<book>"
			+   "<author>"
			+     "<name>Douglas Adams</name>"
			+   "</author>"
			+   "<author>"
			+     "<name>Author 2</name>"
			+   "</author>"
			+   "<description>Science fiction comedy book</description>"
			+   "<isbn>1-84023-742-2</isbn>"
			+   "<nbOfPage>354</nbOfPage>"
			+   "<title>The Hitchhiker's Guide to the Galaxy</title>"
			+ "</book>";
	private static final String XML_BOOKS = 
			  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
			+ "<books>"
			+ "<book>"
			+   "<author>"
			+     "<name>Douglas Adams</name>"
			+   "</author>"
			+   "<description>Science fiction comedy book</description>"
			+   "<isbn>1-84023-742-2</isbn>"
			+   "<nbOfPage>354</nbOfPage>"
			+   "<title>The Hitchhiker's Guide to the Galaxy</title>"
			+ "</book>"
			+ "<book>"
			+   "<author>"
			+     "<name>Terry Pratchett</name>"
			+   "</author>"
			+   "<author>"
			+     "<name>Neil Gaiman</name>"
			+   "</author>"
			+   "<description>The Nice and Accurate Prophecies of Agnes Nutter, Witch</description>"
			+   "<isbn>0-575-04800-X</isbn>"
			+   "<nbOfPage>288</nbOfPage>"
			+   "<title>Good omens</title>"
			+ "</book>"
			+ "</books>";

	@Test
	public void shouldMarshallABook() throws JAXBException {
		// given
		Book book = new Book("The Hitchhiker's Guide to the Galaxy", "Science fiction comedy book", "1-84023-742-2", 354);
		book.getAuthors().add(new Author("Douglas Adams"));
		book.getAuthors().add(new Author("Author 2"));
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(Book.class);
		Marshaller m = context.createMarshaller();
		m.marshal(book, writer);
		
		// then
		assertEquals(XML, writer.toString());
	}

	@Test
	public void shouldMarshallAListOfBooks() throws JAXBException {
		Books books = new Books();
		Book book = new Book(
				"The Hitchhiker's Guide to the Galaxy", 
				"Science fiction comedy book", "1-84023-742-2", 354
		);
		book.getAuthors().add(new Author("Douglas Adams"));
		books.add(book);
		Book book2 = new Book(
				"Good omens", 
				"The Nice and Accurate Prophecies of Agnes Nutter, Witch", "0-575-04800-X", 288
		);
		book2.getAuthors().add(new Author("Terry Pratchett"));
		book2.getAuthors().add(new Author("Neil Gaiman"));
		books.add(book2);
		StringWriter writer = new StringWriter();
		Class<?>[] classes = new Class[2];
		classes[0] = Books.class;
		classes[1] = Book.class;
		JAXBContext context = JAXBContext.newInstance(classes);
		Marshaller m = context.createMarshaller();
		m.marshal(books, writer);
		
		assertEquals(XML_BOOKS, writer.toString());
	}
	
	@Test
	public void shouldCreateUpdateAndDeleteABook() throws JAXBException {
		
		Book book = new Book(
				"The Hitchhiker's Guide to the Galaxy", 
				"Science fiction comedy book", "1-84023-742-2", 354
		);

		// POSTs (creates) a Book
		Response response = target.request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		response.close();
		URI bookURI = response.getLocation();

		// PUTs (updates) the book
		String bookId = bookURI.toString().split("/")[6];
		Book updatedBook = new Book(
				Long.parseLong(bookId),    								   // <- Same id, 
				"The Hitchhiker's Guide",  								   // <- updated title, 
				"Science fiction comedy book", "1-84023-742-2", 354		   // <- other info same.
		); 
		response = target.request()
				.put(Entity.entity(updatedBook, MediaType.APPLICATION_XML));
		assertEquals("OK", response.getStatusInfo().toString());
		response.close();
		
		// GETs the book by location, confirms the updated title
		response = client.target(bookURI).request().get();
		book = response.readEntity(Book.class);
		assertEquals("OK", response.getStatusInfo().toString());
		assertEquals("The Hitchhiker's Guide", book.getTitle());
		response.close();

		// GETs the book id and DELETEs it
		response = target.path(bookId).request().delete();
		assertEquals("No Content", response.getStatusInfo().toString());
		response.close();

		// GETs the Book by location and confirms it has been deleted
		response = client.target(bookURI).request().get();
		assertEquals("Not Found", response.getStatusInfo().toString());
		response.close();
	}

	@Test
	public void shouldNotFindInvalidBookID() throws JAXBException {

		// GETs a Book with an unknown ID
		Response response = target.path("invalidID").request().get();
		assertEquals("Not Found", response.getStatusInfo().toString());
		response.close();
	}
}