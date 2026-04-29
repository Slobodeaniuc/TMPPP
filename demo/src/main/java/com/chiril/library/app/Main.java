package com.chiril.library.app;

import com.chiril.library.factory.*;
import com.chiril.library.repo.Catalog;
import com.chiril.library.repo.InMemoryCatalog;
import com.chiril.library.repo.InMemoryLoanRepository;
import com.chiril.library.repo.LoanRepository;
import com.chiril.library.service.LibraryService;
import com.chiril.library.service.LoanPolicy;
import com.chiril.library.domain.LibraryItem;

public final class Main {
        public static void main(String[] args) {

                // ABSTRACT FACTORY
                LibraryAbstractFactory factory = new StandardLibraryFactory();
                // Poți schimba cu: new ShortLoanLibraryFactory();

                Catalog catalog = InMemoryCatalog.getInstance();
                LoanRepository loans = InMemoryLoanRepository.getInstance();
                LoanPolicy policy = factory.loanPolicy();

                LibraryService service = new LibraryService(catalog, loans, policy);

                LibraryItem book = factory.bookCreator().create(
                                ItemRequest.builder(ItemType.BOOK, "B1", "Clean Code")
                                                .author("Robert C. Martin")
                                                .isbn("978-0132350884")
                                                .build());
                LibraryItem magazine = factory.magazineCreator().create(
                                ItemRequest.builder(ItemType.MAGAZINE, "M1", "National Geographic")
                                                .issueNumber(202)
                                                .build());
                LibraryItem dvd = factory.dvdCreator().create(
                                ItemRequest.builder(ItemType.DVD, "D1", "Interstellar")
                                                .durationMinutes(169)
                                                .build());

                catalog.addItem(book);
                catalog.addItem(magazine);
                catalog.addItem(dvd);

                // Demonstrație clonare
                LibraryItem clonedBook = book.clone();
                LibraryItem clonedMagazine = magazine.clone();
                LibraryItem clonedDvd = dvd.clone();

                System.out.println("\n=== Clonare produse ===");
                System.out.println("Carte originala:   " + book);
                System.out.println("Carte clonata:     " + clonedBook);
                System.out.println("Revista originala: " + magazine);
                System.out.println("Revista clonata:   " + clonedMagazine);
                System.out.println("DVD original:      " + dvd);
                System.out.println("DVD clonat:        " + clonedDvd);

                String memberId = "U1";

                System.out.println("=== Borrow B1 ===");
                System.out.println(service.borrowItem(memberId, "B1"));

                System.out.println("\n=== Borrow M1 ===");
                System.out.println(service.borrowItem(memberId, "M1"));

                System.out.println("\n=== Return B1 ===");
                System.out.println(service.returnItem("B1"));

                System.out.println("\n=== Loans for member U1 ===");
                service.listLoansForMember(memberId).forEach(System.out::println);
        }
}
