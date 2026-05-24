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
    // {id, title, author, isbn}
    private static final String[][] DEMO_BOOKS = {
            {"B1",  "Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin",      "978-0132350884"},
            {"B2",  "Clean Architecture: A Craftsman's Guide",                "Robert C. Martin",      "978-0134494166"},
            {"B3",  "The Pragmatic Programmer: Your Journey to Mastery",      "David Thomas",          "978-0135957059"},
            {"B4",  "Effective Java",                                          "Joshua Bloch",          "978-0134685991"},
            {"B5",  "Design Patterns: Elements of Reusable OO Software",      "Erich Gamma",           "978-0201633610"},
            {"B6",  "Refactoring: Improving the Design of Existing Code",     "Martin Fowler",         "978-0201485677"},
            {"B7",  "Domain-Driven Design: Tackling Complexity in Software",  "Eric Evans",            "978-0321125217"},
            {"B8",  "Working Effectively with Legacy Code",                   "Michael C. Feathers",   "978-0131177055"},
            {"B9",  "Code Complete: A Practical Handbook",                    "Steve McConnell",       "978-0735619678"},
            {"B10", "Introduction to Algorithms",                             "Thomas H. Cormen",      "978-0262046305"},
            {"B11", "The Mythical Man-Month",                                 "Frederick P. Brooks Jr","978-0201835953"},
            {"B12", "Patterns of Enterprise Application Architecture",        "Martin Fowler",         "978-0321127426"},
            {"B13", "Test-Driven Development: By Example",                    "Kent Beck",             "978-0321146533"},
            {"B14", "Continuous Delivery",                                    "Jez Humble",            "978-0321601919"},
            {"B15", "The Art of Computer Programming, Vol. 1",               "Donald E. Knuth",       "978-0201896831"},
    };

    // {id, title, issueNumber}
    private static final String[][] DEMO_MAGAZINES = {
            {"M1",  "IEEE Spectrum"},
            {"M2",  "IEEE Spectrum"},
            {"M3",  "IEEE Spectrum"},
            {"M4",  "Scientific American"},
            {"M5",  "Scientific American"},
            {"M6",  "Scientific American"},
            {"M7",  "MIT Technology Review"},
            {"M8",  "MIT Technology Review"},
            {"M9",  "MIT Technology Review"},
            {"M10", "Harvard Business Review"},
            {"M11", "Harvard Business Review"},
            {"M12", "Harvard Business Review"},
            {"M13", "Wired"},
            {"M14", "Wired"},
            {"M15", "Wired"},
    };
    private static final int[] DEMO_MAGAZINE_ISSUES = {652, 653, 654, 328, 329, 330, 126, 127, 128, 100, 101, 102, 31, 32, 33};

    // {id, title, durationMinutes}
    private static final String[][] DEMO_DVDS = {
            {"D1",  "The Shawshank Redemption (1994)"},
            {"D2",  "The Godfather (1972)"},
            {"D3",  "The Dark Knight (2008)"},
            {"D4",  "Schindler's List (1993)"},
            {"D5",  "Inception (2010)"},
            {"D6",  "Interstellar (2014)"},
            {"D7",  "Pulp Fiction (1994)"},
            {"D8",  "The Lord of the Rings: Return of the King (2003)"},
            {"D9",  "Forrest Gump (1994)"},
            {"D10", "Fight Club (1999)"},
            {"D11", "The Matrix (1999)"},
            {"D12", "Goodfellas (1990)"},
            {"D13", "Blade Runner 2049 (2017)"},
            {"D14", "Gladiator (2000)"},
            {"D15", "The Silence of the Lambs (1991)"},
    };
    private static final int[] DEMO_DVD_DURATIONS = {142, 175, 152, 195, 148, 169, 154, 201, 142, 139, 136, 145, 163, 155, 118};

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
        for (int i = 0; i < DEMO_MAGAZINES.length; i++) {
            String[] mag = DEMO_MAGAZINES[i];
            if (catalog.findById(mag[0]).isEmpty()) {
                addMagazine(mag[0], mag[1], DEMO_MAGAZINE_ISSUES[i]);
            }
        }
    }

    private void ensureDvds() {
        for (int i = 0; i < DEMO_DVDS.length; i++) {
            String[] dvd = DEMO_DVDS[i];
            if (catalog.findById(dvd[0]).isEmpty()) {
                addDvd(dvd[0], dvd[1], DEMO_DVD_DURATIONS[i]);
            }
        }
    }

    private void ensureStarterGroup() {
        if (catalog.findById("G1").isEmpty()) {
            addGroupByItemIds("G1", "Software Engineering Essentials", List.of("B1", "B5", "B6"));
        }
        if (catalog.findById("G2").isEmpty()) {
            addGroupByItemIds("G2", "Classic Cinema Collection", List.of("D1", "D2", "D3"));
        }
        if (catalog.findById("G3").isEmpty()) {
            addGroupByItemIds("G3", "Science & Tech Bundle", List.of("M4", "M7", "M10"));
        }
    }

    private LibraryItem requireCatalogItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId invalid");
        }
        return catalog.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Missing catalog item: " + itemId));
    }
}
