package com.cybercom.librarytest.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JPA bean for representing a book in the library.
 * @author Lennart Moraeus
 */
@Entity
@XmlRootElement
@NamedQueries({
	@NamedQuery(
		name = Book.FIND_ALL, 
		query = "SELECT b FROM Book b"
	), 
	@NamedQuery(
	    name=Book.FIND_ALL_BY_AUTHOR,
	    query="SELECT DISTINCT b "
	    +     "FROM Book b, IN (b.authors) AS a "
	    +     "WHERE a.id = :id"
	)
})
public class Book {

	public static final String FIND_ALL = "Book.findAll";
	public static final String FIND_ALL_BY_AUTHOR = "Book.findAllByAuthor";

	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable = false)
	private String title;
	@Column(length = 2000)
	private String description;
	private String isbn;
	private Integer nbrPages;
	@ManyToMany
	@JoinTable(name="book_has_author")
	private List<Author> authors;

	public Book() {
		this(null, null, null, null, null);
	}

	public Book(String title, String description, String isbn, Integer nbrPages) {
		this(null, title, description, isbn, nbrPages);	
	}
	
	public Book(Long id, String title, String description, String isbn, Integer nbrPages) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.isbn = isbn;
		this.nbrPages = nbrPages;
		this.authors = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Integer getNbOfPage() {
		return nbrPages;
	}

	public void setNbOfPage(Integer nbOfPage) {
		this.nbrPages = nbOfPage;
	}
	
	@XmlElement(name = "author")
	public List<Author> getAuthors() {
		return authors;
	}
	
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}
}
