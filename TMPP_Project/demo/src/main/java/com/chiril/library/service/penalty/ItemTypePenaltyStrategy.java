package com.chiril.library.service.penalty;

import com.chiril.library.domain.Book;
import com.chiril.library.domain.Dvd;
import com.chiril.library.domain.LibraryItem;
import com.chiril.library.domain.LibraryItemGroup;
import com.chiril.library.domain.Loan;
import com.chiril.library.domain.Magazine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class ItemTypePenaltyStrategy implements PenaltyStrategy {
    private static final BigDecimal BOOK_DAILY_RATE = BigDecimal.valueOf(1.50);
    private static final BigDecimal DVD_DAILY_RATE = BigDecimal.valueOf(3.00);
    private static final BigDecimal MAGAZINE_DAILY_RATE = BigDecimal.valueOf(1.00);

    @Override
    public BigDecimal computePenalty(LibraryItem item, Loan loan, LocalDate evaluationDate) {
        if (item == null) {
            throw new IllegalArgumentException("item null");
        }
        if (loan == null) {
            throw new IllegalArgumentException("loan null");
        }
        if (evaluationDate == null) {
            throw new IllegalArgumentException("evaluationDate null");
        }
        if (item instanceof LibraryItemGroup) {
            throw new IllegalArgumentException("Composite items cannot be penalized directly: " + item.getId());
        }
        if (!evaluationDate.isAfter(loan.getDueDate())) {
            return BigDecimal.ZERO;
        }

        long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), evaluationDate);
        return dailyRateFor(item).multiply(BigDecimal.valueOf(overdueDays));
    }

    private static BigDecimal dailyRateFor(LibraryItem item) {
        if (item instanceof Book) {
            return BOOK_DAILY_RATE;
        }
        if (item instanceof Dvd) {
            return DVD_DAILY_RATE;
        }
        if (item instanceof Magazine) {
            return MAGAZINE_DAILY_RATE;
        }
        throw new IllegalArgumentException("Unsupported item type for penalty: " + item.getClass().getSimpleName());
    }
}
