package com.cybercom.librarytest;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.cybercom.librarytest.model.Author;
import com.cybercom.librarytest.model.Authors;
import com.cybercom.librarytest.model.Book;
import com.cybercom.librarytest.model.Books;

public class TestData {
	public static void initTestData(EntityManager em) {
		Author margaretAtwood = new Author(1L, "Margaret Atwood");
		Author neilGaiman = new Author(2L, "Neil Gaiman");
		Author terryPratchett = new Author(3L, "Terry Pratchett");
		Author arthurCClarke = new Author(4L, "Arthur C. Clarke");
		Author stephenBaxter = new Author(5L, "Stephen Baxter");

		Book oryxAndCrake = new Book(
				"Oryx and Crake", 
				"The novel focuses on a post-apocalyptic character named Snowman, living near a group of primitive human-like creatures whom he calls Crakers.",
				"0-7710-0868-6", 
				411
		);
		oryxAndCrake.getAuthors().add(margaretAtwood);
		Book goodOmens = new Book(
				"Good Omens", 
				"It is the coming of the End Times: the Apocalypse is near, and Final Judgement will soon descend upon the human species. This comes as a bit of bad news to the angel Aziraphale (who was the guardian of the Eastern Gate of Eden) and the demon Crowley (who, when he was originally named Crawly, was the serpent who tempted Eve to eat the apple), respectively the representatives of Heaven and Hell on Earth, as they have become used to living their cozy, comfortable lives and have, in a perverse way, taken a liking to humanity.",
				"0-575-04800-X", 
				288
		);
		goodOmens.getAuthors().add(neilGaiman);
		goodOmens.getAuthors().add(terryPratchett);
		Book guardsGuards = new Book(
				"Guards! Guards!", 
				"The story follows a plot by a secret brotherhood, the Unique and Supreme Lodge of the Elucidated Brethren of the Ebon Night, to overthrow the Patrician of Ankh-Morpork and install a puppet king, under the control of the Supreme Grand Master. Using a stolen magic book, they summon a dragon to strike fear into the people of Ankh-Morpork.",
				"0-575-04606-6", 
				288
		);
		guardsGuards.getAuthors().add(terryPratchett);
		Book timesEye = new Book(
				"Time's Eye", 
				"The story opens with two hominids, probably Homo erectus, known as 'Seeker', a mother, and her infant daughter 'Grasper'. As they walked on the tranquil Earth two million years ago, they were suddenly captured by some blood-red beings, who turn out to be nineteenth-century British redcoat soldiers.",
				"0-00-713846-6", 
				392
		);
		timesEye.getAuthors().add(arthurCClarke);
		timesEye.getAuthors().add(stephenBaxter);
		Book _2001ASpaceOdyssey = new Book(
				"2001: A Space Odyssey", 
				"In the background to the story in the book, an ancient and unseen alien race uses a device with the appearance of a large crystalline monolith to investigate worlds all across the galaxy and, if possible, to encourage the development of intelligent life.",
				"0-453-00269-2", 
				221
		);
		_2001ASpaceOdyssey.getAuthors().add(arthurCClarke);
		Book rendezvousWithRama = new Book(
				"Rendezvous with Rama", 
				"After a meteorite falls in Northeast Italy in 2077, creating a major disaster, the government of Earth sets up the Spaceguard system as an early warning of arrivals from deep space.",
				"0-575-01587-X", 
				256
		);
		rendezvousWithRama.getAuthors().add(arthurCClarke);
		Book neverwhere = new Book(
				"Neverwhere", 
				"Neverwhere is the story of Richard Mayhew and his trials and tribulations in London. At the start of the story, he is a young businessman, recently moved from Scotland and with a normal life ahead. This breaks, however, when he stops to help a mysterious young girl who appears before him, bleeding and weakened, as he walks with his fiancée to dinner to meet her influential boss.",
				"0-7472-6668-9", 
				387
		);
		neverwhere.getAuthors().add(neilGaiman);
		Book americanGods = new Book(
				"American Gods", 
				"The central premise of the novel is that gods and mythological creatures exist because people believe in them (a form of thoughtform). Immigrants to the United States brought with them spirits and gods. However, the power of these mythological beings has diminished as people's beliefs wane. New gods have arisen, reflecting America's obsessions with media, celebrity, technology, and drugs, among others.",
				"0-380-97365-0", 
				465
		);
		americanGods.getAuthors().add(neilGaiman);
		Book coraline = new Book(
				"Coraline", 
				"Coraline Jones and her parents move into an old house that has been divided into flats. The other tenants include Miss Spink and Miss Forcible, two elderly women retired from the stage, and Mr. Bobinsky, initially referred to as \"the crazy old man upstairs\", who claims to be training a mouse circus. The flat beside Coraline's is unoccupied.",
				"0-06-113937-8", 
				163
		);
		coraline.getAuthors().add(neilGaiman);
		Book anansiBoys = new Book(
				"Anansi Boys", 
				"Anansi Boys is the story of Charles \"Fat Charlie\" Nancy, a timid Londoner devoid of ambition, whose unenthusiastic wedding preparations are disrupted when he learns of his father's death in Florida. The flamboyant Mr. Nancy, in whose shadow Fat Charlie has always lived, died in a typically embarrassing manner by suffering a fatal heart attack while singing to a young woman on stage in a karaoke bar.",
				"0-06-051518-X", 
				400
		);
		anansiBoys.getAuthors().add(neilGaiman);
		
		Books books = new Books();
		books.add(oryxAndCrake);
		books.add(goodOmens);
		books.add(guardsGuards);
		books.add(timesEye);
		books.add(_2001ASpaceOdyssey);
		books.add(rendezvousWithRama);
		books.add(neverwhere);
		books.add(americanGods);
		books.add(coraline);
		books.add(anansiBoys);

		Authors authors = new Authors();
		authors.add(margaretAtwood);
		authors.add(neilGaiman);
		authors.add(terryPratchett);
		authors.add(arthurCClarke);
		authors.add(stephenBaxter);
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		for (Author author : authors) {
			em.persist(author);
		}
		for (Book book : books) {
			em.persist(book);
		}
		tx.commit();
		em.close();
	}
}
