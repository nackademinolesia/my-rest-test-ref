package com.cybercom.librarytest;

import org.junit.After;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response; 

/**
 * Base class for integration tests.
 * @author Lennart Moraeus
 */
public abstract class RestServiceIntegrationTest {

	protected static final String TEST_AUTHOR_NAME_1 = "Author name 1";
	protected static final String TEST_AUTHOR_NAME_2 = "Author name 2";
	protected static final String TEST_BOOK_TITLE = "Book title BookAuthorRestServiceIT";
	protected static final int TEST_BOOK_NBRPAGES = 354;
	protected static final String TEST_BOOK_ISBN = "1-84023-742-2";
	protected static final String TEST_BOOK_DESCRIPTION = "Book description";
	
	protected static final String BOOK_BASE_URI =
			"http://localhost:8080/librarytest/rest/books";
	protected static final String AUTHOR_BASE_URI =
			"http://localhost:8080/librarytest/rest/authors";
	
	protected static Client client = ClientBuilder.newClient();
	protected static Response response;
	
	/**
	 * Closes all open connections. Useful if the test asserted or crashed 
	 * in the middle of a transaction. 
	 */
	@After
	public void cleanUpAfter() {
		if (response != null) {
			response.close();
		}
	}
}