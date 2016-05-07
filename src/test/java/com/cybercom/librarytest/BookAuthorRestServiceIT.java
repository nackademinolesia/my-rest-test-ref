package com.cybercom.librarytest;

import org.junit.Before;
import org.junit.Test;

import com.cybercom.librarytest.model.Author;
import com.cybercom.librarytest.model.Authors;
import com.cybercom.librarytest.model.Book;
import com.cybercom.librarytest.model.Books;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests for the REST service focusing on the Book/Author relationship.
 * @author Lennart Moraeus
 */
public class BookAuthorRestServiceIT extends RestServiceIntegrationTest {

	@Test
	public void shouldReturnAuthorsOfBook() throws JAXBException {
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author1 = new Author(TEST_AUTHOR_NAME_1);
		Author author2 = new Author(TEST_AUTHOR_NAME_2);
		
		// Create two authors
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author1, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId1 = response.getLocation().toString().split("/")[6];
		response.close();
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author2, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId2 = response.getLocation().toString().split("/")[6];
		response.close();
		author1.setId(Long.parseLong(authorId1));
		author2.setId(Long.parseLong(authorId2));
		
		// Create the book, with the authors included
		book.getAuthors().add(author1);
		book.getAuthors().add(author2);
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		response.close();
		
		// Get the authors of the book by book id, confirm that the 
		// authors are present in the list
		response = client.target(BOOK_BASE_URI)
				.path(bookId)
				.path("authors")
				.request()
				.get();
		assertEquals("OK", response.getStatusInfo().toString());
		Authors authors = response.readEntity(Authors.class);
		response.close();
		assertEquals(2, authors.size());
		assertEquals(TEST_AUTHOR_NAME_1, authors.get(0).getName());
		assertEquals(TEST_AUTHOR_NAME_2, authors.get(1).getName());
	}
	
	/**
	 * Adds author to book using POST /books/{book_id}/authors
	 * @throws JAXBException
	 */
	@Test
	public void shouldAddExistingAuthorToBook() throws JAXBException {
		
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author = new Author(TEST_AUTHOR_NAME_1);
		
		// Create the book
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		response.close();
		
		// Create an author
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId = response.getLocation().toString().split("/")[6];
		author.setId(Long.parseLong(authorId));
		response.close();
		
		// Add the author to the book
		response = client.target(BOOK_BASE_URI)
				.path(bookId)
				.path("authors")
				.request()
				.post(Entity.entity(author, MediaType.APPLICATION_XML));
		assertEquals("OK", response.getStatusInfo().toString());
		response.close();
		
		// Get the book by id, confirm that the author was added
		response = client.target(BOOK_BASE_URI).path(bookId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		List<Author> authors = response.readEntity(Book.class).getAuthors();
		assertEquals(1, authors.size());
		assertEquals(TEST_AUTHOR_NAME_1, authors.get(0).getName());
		response.close();
		
		// Get all books by the author, confirm that the book is in the list
		response = client.target(BOOK_BASE_URI+"/byauthor")
				.path(authorId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		Books books = response.readEntity(Books.class);
		assertEquals(1, books.size());
		assertEquals(TEST_BOOK_TITLE, books.get(0).getTitle());
		response.close();		
	}
	
	/**
	 * Tries to add author to book using POST /books/{book_id}/authors
	 * @throws JAXBException
	 */
	@Test
	public void shouldNotAddNonExistingAuthorToBook() throws JAXBException {
		
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author = new Author(TEST_AUTHOR_NAME_1);
		
		// Create the book
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		response.close();
		
		// Add a non-existing author to the book, confirm that an 
		// error is thrown
		response = client.target(BOOK_BASE_URI)
				.path(bookId)
				.path("authors")
				.request()
				.post(Entity.entity(author, MediaType.APPLICATION_XML));
		assertEquals("Bad Request", response.getStatusInfo().toString());
		response.close();
		
		// Get the book by id, confirm that the author was not added
		response = client.target(BOOK_BASE_URI).path(bookId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		List<Author> authors = response.readEntity(Book.class).getAuthors();
		assertEquals(0, authors.size());
		response.close();		
	}
	
	@Test
	public void shouldAddExistingAuthorToBookByUpdatingBook() throws JAXBException {
		
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author = new Author(TEST_AUTHOR_NAME_1);
		
		// Create the book
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		response.close();
		
		// Create an author
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId = response.getLocation().toString().split("/")[6];
		author.setId(Long.parseLong(authorId));
		response.close();
		
		// Add the author to the book
		Book updatedBook = new Book(Long.parseLong(bookId), TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		updatedBook.getAuthors().add(author);
		response = client.target(BOOK_BASE_URI).request()
				.put(Entity.entity(updatedBook, MediaType.APPLICATION_XML));
		assertEquals("OK", response.getStatusInfo().toString());
		response.close();
		
		// Get the book by id, confirm that the author was added
		response = client.target(BOOK_BASE_URI).path(bookId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		List<Author> authors = response.readEntity(Book.class).getAuthors();
		assertEquals(1, authors.size());
		assertEquals(TEST_AUTHOR_NAME_1, authors.get(0).getName());
		response.close();
		
		// Get all books by the author, confirm that the book is in the list
		response = client.target(BOOK_BASE_URI+"/byauthor")
				.path(authorId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		Books books = response.readEntity(Books.class);
		assertEquals(1, books.size());
		assertEquals(TEST_BOOK_TITLE, books.get(0).getTitle());
		response.close();		
	}
	
	@Test
	public void shouldNotAddNonExistingAuthorToBookByUpdatingBook() throws JAXBException {
		
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author = new Author(TEST_AUTHOR_NAME_1);
		
		// Create the book
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		response.close();
		
		// Add an author to the book that has no id field set thus does not 
		// exist in the database, confirm that an error is thrown
		Book updatedBook = new Book(Long.parseLong(bookId), TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		updatedBook.getAuthors().add(author);
		response = client.target(BOOK_BASE_URI).request()
				.put(Entity.entity(updatedBook, MediaType.APPLICATION_XML));
		response.close();
		assertEquals("Bad Request", response.getStatusInfo().toString());
		
		// Get the book by id, confirm that the author was not added
		response = client.target(BOOK_BASE_URI).path(bookId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		List<Author> authors = response.readEntity(Book.class).getAuthors();
		assertEquals(0, authors.size());
	}
	
	/**
	 * Updates book with authors using PUT /books/{book_id}/authors
	 * @throws JAXBException
	 */
	@Test
	public void shouldUpdateBookWithExistingAuthors() throws JAXBException {
		
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author1 = new Author(TEST_AUTHOR_NAME_1);
		Author author2 = new Author(TEST_AUTHOR_NAME_2);
		
		// Create the book
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		response.close();
		
		// Create two authors
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author1, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId1 = response.getLocation().toString().split("/")[6];
		response.close();
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author2, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId2 = response.getLocation().toString().split("/")[6];
		response.close();
		author1.setId(Long.parseLong(authorId1));
		author2.setId(Long.parseLong(authorId2));
		
		// Update the book with the authors
		Authors authors = new Authors();
		authors.add(author1);
		authors.add(author2);
		response = client.target(BOOK_BASE_URI)
				.path(bookId)
				.path("authors")
				.request()
				.put(Entity.entity(authors, MediaType.APPLICATION_XML));
		assertEquals("OK", response.getStatusInfo().toString());
		response.close();
		
		// Get the book by id, confirm that the authors were added
		response = client.target(BOOK_BASE_URI).path(bookId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		authors = new Authors(response.readEntity(Book.class).getAuthors());
		assertEquals(2, authors.size());
		Collections.sort(authors);
		assertEquals(TEST_AUTHOR_NAME_1, authors.get(0).getName());
		assertEquals(TEST_AUTHOR_NAME_2, authors.get(1).getName());
		response.close();		
	}
	
	/**
	 * Tries to update book with authors using PUT /books/{book_id}/authors
	 * @throws JAXBException
	 */
	@Test
	public void shouldNotUpdateBookWithNonExistingAuthors() throws JAXBException {
		
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author1 = new Author(TEST_AUTHOR_NAME_1);
		Author author2 = new Author(TEST_AUTHOR_NAME_2);
		
		// Create the book
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		response.close();
		
		// Create one author
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author1, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId1 = response.getLocation().toString().split("/")[6];
		response.close();
		author1.setId(Long.parseLong(authorId1));
		author2.setId(Long.parseLong(authorId1)); // <- same id for author2
		
		// Update the book with the authors
		// Note that only author1 has been added to the database
		Authors authors = new Authors();
		authors.add(author1);
		authors.add(author2);
		response = client.target(BOOK_BASE_URI)
				.path(bookId)
				.path("authors")
				.request()
				.put(Entity.entity(authors, MediaType.APPLICATION_XML));
		assertEquals("Bad Request", response.getStatusInfo().toString());
		response.close();
		
		// Get the book by id, confirm that the authors were not added
		response = client.target(BOOK_BASE_URI).path(bookId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		authors = new Authors(response.readEntity(Book.class).getAuthors());
		assertEquals(0, authors.size());
		response.close();		
	}
	
	@Test
	public void shouldCreateBookWithExistingAuthor() throws JAXBException {
		
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author = new Author(TEST_AUTHOR_NAME_1);
		
		// Create an author
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId = response.getLocation().toString().split("/")[6];
		author.setId(Long.parseLong(authorId));
		response.close();
		
		// Create the book, with the author included
		book.getAuthors().add(author);
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		response.close();
		
		// Get the book by id, confirm that the author was added
		response = client.target(BOOK_BASE_URI).path(bookId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		List<Author> authors = response.readEntity(Book.class).getAuthors();
		assertEquals(1, authors.size());
		assertEquals(TEST_AUTHOR_NAME_1, authors.get(0).getName());
		response.close();
		
		// Get all books by the author, confirm that the book is in the list
		response = client.target(BOOK_BASE_URI+"/byauthor")
				.path(authorId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		Books books = response.readEntity(Books.class);
		assertEquals(1, books.size());
		assertEquals(TEST_BOOK_TITLE, books.get(0).getTitle());
		response.close();	
	}
	
	@Test
	public void shouldNotCreateBookWithNonExistingAuthor() throws JAXBException {
		
		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author = new Author(TEST_AUTHOR_NAME_1);
		
		// Create the book, with an author included that has no id value 
		// and thus is not yet in the database, confirm that an error is thrown.
		book.getAuthors().add(author);
		Response response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Bad Request", response.getStatusInfo().toString());
		response.close();
	}
	
	@Test
	public void shouldRemoveAuthorFromBook() throws JAXBException {

		Book book = new Book(TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		Author author = new Author(TEST_AUTHOR_NAME_1);
		
		// Create an author
		response = client.target(AUTHOR_BASE_URI).request()
				.post(Entity.entity(author, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String authorId = response.getLocation().toString().split("/")[6];
		author.setId(Long.parseLong(authorId));
		response.close();
		
		// Create the book, with the author included
		book.getAuthors().add(author);
		response = client.target(BOOK_BASE_URI).request()
				.post(Entity.entity(book, MediaType.APPLICATION_XML));
		assertEquals("Created", response.getStatusInfo().toString());
		String bookId = response.getLocation().toString().split("/")[6];
		book.setId(Long.parseLong(bookId));
		response.close();
		
		// Remove the author from the book
		Book updatedBook = new Book(book.getId(), TEST_BOOK_TITLE, 
				TEST_BOOK_DESCRIPTION, TEST_BOOK_ISBN, TEST_BOOK_NBRPAGES);
		response = client.target(BOOK_BASE_URI).request()
				.put(Entity.entity(updatedBook, MediaType.APPLICATION_XML));
		assertEquals("OK", response.getStatusInfo().toString());
		response.close();
		
		// Get the book by id, confirm that the author was removed.
		response = client.target(BOOK_BASE_URI).path(bookId).request().get();
		assertEquals("OK", response.getStatusInfo().toString());
		List<Author> authors = response.readEntity(Book.class).getAuthors();
		assertEquals(0, authors.size());
		response.close();
	}
	
	/**
	 * Removes from the database all data generated by these tests.
	 */
	@Before
	public void cleanUpBefore() {
		
		// Books
		Response response = client.target(BOOK_BASE_URI).request().get();
		if (response.hasEntity()) {
			Books books = response.readEntity(Books.class);
			response.close();
			for (Book b : books) {
				if (b.getTitle().startsWith(TEST_BOOK_TITLE)) {
					response = 
							client.target(BOOK_BASE_URI)
							.path(String.valueOf(b.getId()))
							.request()
							.delete();
					response.close();
				}
			}
		}
		
		// Authors
		response = client.target(AUTHOR_BASE_URI).request().get();
		if (response.hasEntity()) {
			Authors authors = response.readEntity(Authors.class);
			response.close();
			for (Author a : authors) {
				if (a.getName().startsWith(TEST_AUTHOR_NAME_1) || 
						a.getName().startsWith(TEST_AUTHOR_NAME_2)) {
					response = 
							client.target(AUTHOR_BASE_URI)
							.path(String.valueOf(a.getId()))
							.request()
							.delete();
					response.close();
				}
			}
		}
	}
}