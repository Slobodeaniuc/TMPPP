package com.chiril.library.factory;

import com.chiril.library.domain.LibraryItem;
import com.chiril.library.service.LoanPolicy;
import com.chiril.library.service.decorator.ItemTypeLoanPolicyDecorator;
import com.chiril.library.service.decorator.WeekendAdjustmentLoanPolicyDecorator;
import com.chiril.library.service.penalty.GracePeriodPenaltyStrategy;
import com.chiril.library.service.penalty.ItemTypePenaltyStrategy;
import com.chiril.library.service.penalty.PenaltyStrategy;

import java.time.LocalDate;

public final class ShortLoanLibraryFactory implements LibraryAbstractFactory {

    @Override
    public LibraryItemCreator bookCreator() {
        return new BookCreator();
    }

    @Override
    public LibraryItemCreator magazineCreator() {
        return new MagazineCreator();
    }

    @Override
    public LibraryItemCreator dvdCreator() {
        return new DvdCreator();
    }

    @Override
    public LibraryItemCreator groupCreator() {
        return new GroupCreator();
    }

    @Override
    public LoanPolicy loanPolicy() {
        // policy “din fabrică” (familie)
        LoanPolicy basePolicy = new LoanPolicy() {
            @Override
            public LocalDate computeDueDate(LibraryItem item, LocalDate loanDate) {
                // minim: împrumut mai scurt pentru toate
                return loanDate.plusDays(7);
            }
        };

        LoanPolicy itemAwarePolicy = new ItemTypeLoanPolicyDecorator(basePolicy);
        return new WeekendAdjustmentLoanPolicyDecorator(itemAwarePolicy);
    }

    @Override
    public PenaltyStrategy penaltyStrategy() {
        return new GracePeriodPenaltyStrategy(new ItemTypePenaltyStrategy(), 2);
    }
}
