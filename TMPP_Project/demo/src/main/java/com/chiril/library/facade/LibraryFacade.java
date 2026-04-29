package com.chiril.library.facade;

import com.chiril.library.adapter.FileCatalogAdapter;
import com.chiril.library.adapter.FileLoanRepositoryAdapter;
import com.chiril.library.adapter.storage.FileStorage;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.Loan;
import com.chiril.library.domain.ReturnReceipt;
import com.chiril.library.factory.ItemRequest;
import com.chiril.library.factory.ItemType;
import com.chiril.library.factory.LibraryAbstractFactory;
import com.chiril.library.iterator.CatalogNavigator;
import com.chiril.library.iterator.LibraryIterator;
import com.chiril.library.notification.BorrowLoanNotification;
import com.chiril.library.notification.ConsoleNotificationChannel;
import com.chiril.library.notification.LoanNotification;
import com.chiril.library.notification.NotificationChannel;
import com.chiril.library.notification.ReturnLoanNotification;
import com.chiril.library.memento.BorrowCartService;
import com.chiril.library.observer.LibraryObserver;
import com.chiril.library.observer.PenaltyObserver;
import com.chiril.library.repo.Catalog;
import com.chiril.library.repo.LoanRepository;
import com.chiril.library.repo.proxy.AuditedLoanRepositoryProxy;
import com.chiril.library.service.LibraryService;
import com.chiril.library.service.exceptions.LoanNotFoundException;
import com.chiril.library.service.penalty.PenaltyService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class LibraryFacade {
    private static final int[] DEMO_MAGAZINE_ISSUES = {202, 203, 204, 205, 206};
    private static final int[] DEMO_DVD_DURATIONS = {169, 148, 136, 121, 117};
    private static final String[][] DEMO_BOOKS = {
            {"B1", "Clean Code", "Robert C. Martin", "978-0132350884"},
            {"B2", "Clean Code", "Robert C. Martin", "978-0132350884"},
            {"B3", "Clean Code", "Robert C. Martin", "978-0132350884"},
            {"B4", "Effective Java", "Joshua Bloch", "978-0134685991"},
            {"B5", "Effective Java", "Joshua Bloch", "978-0134685991"},
            {"B6", "Effective Java", "Joshua Bloch", "978-0134685991"},
            {"B7", "Design Patterns", "Erich Gamma", "978-0201633610"},
            {"B8", "Design Patterns", "Erich Gamma", "978-0201633610"},
            {"B9", "Design Patterns", "Erich Gamma", "978-0201633610"},
            {"B10", "Refactoring", "Martin Fowler", "978-0201485677"},
            {"B11", "Refactoring", "Martin Fowler", "978-0201485677"},
            {"B12", "Refactoring", "Martin Fowler", "978-0201485677"},
            {"B13", "The Pragmatic Programmer", "Andrew Hunt", "978-0135957059"},
            {"B14", "The Pragmatic Programmer", "Andrew Hunt", "978-0135957059"},
            {"B15", "The Pragmatic Programmer", "Andrew Hunt", "978-0135957059"}
    };
    private static final String[][] DEMO_MAGAZINES = {
            {"M1", "National Geographic"},
            {"M2", "National Geographic"},
            {"M3", "National Geographic"},
            {"M4", "Scientific American"},
            {"M5", "Scientific American"},
            {"M6", "Scientific American"},
            {"M7", "Time"},
            {"M8", "Time"},
            {"M9", "Time"},
            {"M10", "Wired"},
            {"M11", "Wired"},
            {"M12", "Wired"},
            {"M13", "The Economist"},
            {"M14", "The Economist"},
            {"M15", "The Economist"}
    };
    private static final String[][] DEMO_DVDS = {
            {"D1", "Interstellar"},
            {"D2", "Interstellar"},
            {"D3", "Interstellar"},
            {"D4", "Inception"},
            {"D5", "Inception"},
            {"D6", "Inception"},
            {"D7", "The Matrix"},
            {"D8", "The Matrix"},
            {"D9", "The Matrix"},
            {"D10", "The Lord of the Rings: The Fellowship of the Ring"},
            {"D11", "The Lord of the Rings: The Fellowship of the Ring"},
            {"D12", "The Lord of the Rings: The Fellowship of the Ring"},
            {"D13", "The Grand Budapest Hotel"},
            {"D14", "The Grand Budapest Hotel"},
            {"D15", "The Grand Budapest Hotel"}
    };

    private final Catalog catalog;
    private final LibraryAbstractFactory factory;
    private final LibraryService libraryService;
    private final PenaltyService penaltyService;
    private final BorrowCartService borrowCartService;
    private final CatalogNavigator catalogNavigator;

    public LibraryFacade(Catalog catalog, LoanRepository loanRepository, LibraryAbstractFactory factory) {
        this(
            catalog,
            loanRepository,
            factory,
            null,
            null);
        }

        public LibraryFacade(
            Catalog catalog,
            LoanRepository loanRepository,
            LibraryAbstractFactory factory,
            LoanNotification borrowNotification,
            LoanNotification returnNotification) {
        if (catalog == null) {
            throw new IllegalArgumentException("catalog null");
        }
        if (loanRepository == null) {
            throw new IllegalArgumentException("loanRepository null");
        }
        if (factory == null) {
            throw new IllegalArgumentException("factory null");
        }

        this.catalog = catalog;
        this.factory = factory;
        this.penaltyService = new PenaltyService(catalog, loanRepository, factory.penaltyStrategy());
        this.borrowCartService = new BorrowCartService(catalog);
        this.catalogNavigator = new CatalogNavigator(catalog, loanRepository);
        if (borrowNotification == null || returnNotification == null) {
            this.libraryService = new LibraryService(catalog, loanRepository, factory.loanPolicy());
            return;
        }

        this.libraryService = new LibraryService(
                catalog,
                loanRepository,
                factory.loanPolicy(),
                borrowNotification,
                returnNotification);
    }

    public static LibraryFacade fileBacked(Path dataDirectory, LibraryAbstractFactory factory) {
        if (dataDirectory == null) {
            throw new IllegalArgumentException("dataDirectory null");
        }

        Path catalogFile = dataDirectory.resolve("catalog.db");
        Path loansFile = dataDirectory.resolve("loans.db");
        Path loanAuditFile = dataDirectory.resolve("loan-audit.log");

        Catalog catalog = new FileCatalogAdapter(new FileStorage(catalogFile));
        LoanRepository loanRepository = new AuditedLoanRepositoryProxy(
            new FileLoanRepositoryAdapter(new FileStorage(loansFile)),
            loanAuditFile);
        NotificationChannel channel = new ConsoleNotificationChannel();
        LibraryFacade facade = new LibraryFacade(
                catalog,
                loanRepository,
                factory,
                new BorrowLoanNotification(channel),
                new ReturnLoanNotification(channel, catalog, factory.penaltyStrategy()));
        facade.registerObserver(new PenaltyObserver(facade.penaltyService, channel));
        return facade;
    }

    public Loan borrowItem(String memberId, String itemId) {
        return libraryService.borrowItem(memberId, itemId);
    }

    public Loan returnItem(String itemId) {
        return libraryService.returnItem(itemId);
    }

    public ReturnReceipt returnItemWithPenalty(String itemId) {
        LocalDate evaluationDate = LocalDate.now();
        BigDecimal penalty = penaltyService.calculatePenaltyForActiveLoan(itemId, evaluationDate);
        Loan returnedLoan = libraryService.returnItem(itemId);
        return new ReturnReceipt(returnedLoan, penalty, evaluationDate);
    }

    public List<Loan> listLoansForMember(String memberId) {
        return libraryService.listLoansForMember(memberId);
    }

    public BigDecimal calculatePenaltyForLoan(String loanId, LocalDate evaluationDate) {
        return penaltyService.calculatePenaltyForLoan(loanId, evaluationDate);
    }

    public BigDecimal calculatePenaltyForActiveLoan(String itemId, LocalDate evaluationDate) {
        return penaltyService.calculatePenaltyForActiveLoan(itemId, evaluationDate);
    }

    public void registerObserver(LibraryObserver observer) {
        libraryService.registerObserver(observer);
    }

    public void unregisterObserver(LibraryObserver observer) {
        libraryService.unregisterObserver(observer);
    }

    public boolean closeActiveLoanIfPresent(String itemId) {
        try {
            libraryService.returnItem(itemId);
            return true;
        } catch (LoanNotFoundException ex) {
            return false;
        }
    }

    public void addItemToBorrowCart(String memberId, String itemId) {
        borrowCartService.addItem(memberId, itemId);
    }

    public boolean removeItemFromBorrowCart(String memberId, String itemId) {
        return borrowCartService.removeItem(memberId, itemId);
    }

    public List<String> viewBorrowCart(String memberId) {
        return borrowCartService.getCartItems(memberId);
    }

    public boolean undoLastCartChange(String memberId) {
        return borrowCartService.undoLastChange(memberId);
    }

    public void clearBorrowCart(String memberId) {
        borrowCartService.clearCart(memberId);
    }

    public void cancelBorrowCart(String memberId) {
        borrowCartService.resetCart(memberId);
    }

    public List<Loan> checkoutBorrowCart(String memberId) {
        List<String> itemIds = borrowCartService.getCartItems(memberId);
        if (itemIds.isEmpty()) {
            return List.of();
        }

        List<Loan> loans = itemIds.stream()
                .map(itemId -> borrowItem(memberId, itemId))
                .toList();
        borrowCartService.resetCart(memberId);
        return loans;
    }

    public List<LibraryItem> listCatalogItems() {
        return catalog.getAllItems();
    }

    public LibraryIterator<LibraryItem> iterateAvailableBooksByAuthor(String author) {
        return catalogNavigator.iterateAvailableBooksByAuthor(author);
    }

    public List<LibraryItem> findAvailableBooksByAuthor(String author) {
        return catalogNavigator.collect(iterateAvailableBooksByAuthor(author));
    }

    public Optional<LibraryItem> findItemById(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return Optional.empty();
        }
        return catalog.findById(itemId);
    }

    public void addBook(String id, String title, String author, String isbn) {
        catalog.addItem(factory.bookCreator().create(
                ItemRequest.builder(ItemType.BOOK, id, title)
                        .author(author)
                        .isbn(isbn)
                        .build()));
    }

    public void addMagazine(String id, String title, int issueNumber) {
        catalog.addItem(factory.magazineCreator().create(
                ItemRequest.builder(ItemType.MAGAZINE, id, title)
                        .issueNumber(issueNumber)
                        .build()));
    }

    public void addDvd(String id, String title, int durationMinutes) {
        catalog.addItem(factory.dvdCreator().create(
                ItemRequest.builder(ItemType.DVD, id, title)
                        .durationMinutes(durationMinutes)
                        .build()));
    }

    public void addGroupByItemIds(String groupId, String title, List<String> childItemIds) {
        if (childItemIds == null || childItemIds.isEmpty()) {
            throw new IllegalArgumentException("childItemIds invalid");
        }

        ItemRequest.Builder requestBuilder = ItemRequest.builder(ItemType.GROUP, groupId, title);
        for (String childItemId : childItemIds) {
            requestBuilder.child(requireCatalogItem(childItemId));
        }

        catalog.addItem(factory.groupCreator().create(requestBuilder.build()));
    }

    public void ensureDemoCatalog() {
        ensureBooks();
        ensureMagazines();
        ensureDvds();
        ensureStarterGroup();
    }

    private void ensureBooks() {
        for (String[] book : DEMO_BOOKS) {
            if (catalog.findById(book[0]).isEmpty()) {
                addBook(book[0], book[1], book[2], book[3]);
            }
        }
    }

    private void ensureMagazines() {
        for (int index = 0; index < DEMO_MAGAZINES.length; index++) {
            String[] magazine = DEMO_MAGAZINES[index];
            int issueNumber = DEMO_MAGAZINE_ISSUES[index / 3];
            if (catalog.findById(magazine[0]).isEmpty()) {
                addMagazine(magazine[0], magazine[1], issueNumber);
            }
        }
    }

    private void ensureDvds() {
        for (int index = 0; index < DEMO_DVDS.length; index++) {
            String[] dvd = DEMO_DVDS[index];
            int durationMinutes = DEMO_DVD_DURATIONS[index / 3];
            if (catalog.findById(dvd[0]).isEmpty()) {
                addDvd(dvd[0], dvd[1], durationMinutes);
            }
        }
    }

    private void ensureStarterGroup() {
        if (catalog.findById("G1").isPresent()) {
            return;
        }
        addGroupByItemIds("G1", "Starter Bundle", List.of("B1", "M1"));
    }

    private LibraryItem requireCatalogItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId invalid");
        }
        return catalog.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Missing catalog item: " + itemId));
    }
}
