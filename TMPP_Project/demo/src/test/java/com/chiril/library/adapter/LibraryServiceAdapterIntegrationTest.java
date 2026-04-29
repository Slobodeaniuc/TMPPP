package com.chiril.library.adapter;

import com.chiril.library.adapter.storage.FileStorage;
import com.chiril.library.domain.Book;
import com.chiril.library.domain.LibraryItemGroup;
import com.chiril.library.domain.Loan;
import com.chiril.library.repo.Catalog;
import com.chiril.library.repo.LoanRepository;
import com.chiril.library.service.DefaultLoanPolicy;
import com.chiril.library.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LibraryServiceAdapterIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void borrowAndReturn_workAcrossServiceReconstruction() {
        Path catalogFile = tempDir.resolve("catalog.db");
        Path loansFile = tempDir.resolve("loans.db");

        Catalog catalog = new FileCatalogAdapter(new FileStorage(catalogFile));
        LoanRepository loans = new FileLoanRepositoryAdapter(new FileStorage(loansFile));

        catalog.addItem(Book.builder()
                .id("B1")
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .build());

        LibraryService firstService = new LibraryService(catalog, loans, new DefaultLoanPolicy());
        Loan borrowed = firstService.borrowItem("U1", "B1");
        assertNotNull(borrowed.getLoanId());

        LibraryService secondService = new LibraryService(
                new FileCatalogAdapter(new FileStorage(catalogFile)),
                new FileLoanRepositoryAdapter(new FileStorage(loansFile)),
                new DefaultLoanPolicy());

        List<Loan> persistedBeforeReturn = secondService.listLoansForMember("U1");
        assertEquals(1, persistedBeforeReturn.size());
        assertEquals("B1", persistedBeforeReturn.get(0).getItemId());

        secondService.returnItem("B1");

        LibraryService thirdService = new LibraryService(
                new FileCatalogAdapter(new FileStorage(catalogFile)),
                new FileLoanRepositoryAdapter(new FileStorage(loansFile)),
                new DefaultLoanPolicy());

        Loan persistedAfterReturn = thirdService.listLoansForMember("U1").get(0);
        assertFalse(persistedAfterReturn.isActive());
        assertNotNull(persistedAfterReturn.getReturnDate());
    }

    @Test
    void borrowItem_rejectsCompositeGroup() {
        Path catalogFile = tempDir.resolve("catalog.db");
        Path loansFile = tempDir.resolve("loans.db");

        Catalog catalog = new FileCatalogAdapter(new FileStorage(catalogFile));
        LoanRepository loans = new FileLoanRepositoryAdapter(new FileStorage(loansFile));

        catalog.addItem(LibraryItemGroup.builder()
                .id("G1")
                .title("Starter Kit")
                .child(Book.builder()
                        .id("B1")
                        .title("Clean Code")
                        .author("Robert C. Martin")
                        .isbn("978-0132350884")
                        .build())
                .build());

        LibraryService service = new LibraryService(catalog, loans, new DefaultLoanPolicy());

        assertThrows(IllegalArgumentException.class, () -> service.borrowItem("U1", "G1"));
    }
}
