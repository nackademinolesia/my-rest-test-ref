package com.cybercom.librarytest.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JPA bean for representing an author in the library.
 * @author Lennart Moraeus
 */
@Entity
@XmlRootElement
@NamedQuery(name = Author.FIND_ALL, query = "SELECT a FROM Author a")
public class Author implements Comparable<Author> {

	public static final String FIND_ALL = "Author.findAll";

	@Id
	@GeneratedValue
	private Long id;
	@Column(nullable = false)
	private String name;

	public Author() {
		this(null, null);
	}

	public Author(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Author(String name) {
		this(null, name);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Author)) {
			return false;
		}
		Author otherAuthor = (Author)other;
		return this.id.equals(otherAuthor.id) && this.name.equals(otherAuthor.name);
	}
	
	@Override
	public int compareTo(Author other) {
		return this.name.compareTo(other.name);
	}
}
