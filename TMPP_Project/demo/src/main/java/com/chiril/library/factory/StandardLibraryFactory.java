package com.chiril.library.factory;

import com.chiril.library.service.DefaultLoanPolicy;
import com.chiril.library.service.LoanPolicy;
import com.chiril.library.service.decorator.ItemTypeLoanPolicyDecorator;
import com.chiril.library.service.decorator.WeekendAdjustmentLoanPolicyDecorator;
import com.chiril.library.service.penalty.ItemTypePenaltyStrategy;
import com.chiril.library.service.penalty.PenaltyStrategy;

public final class StandardLibraryFactory implements LibraryAbstractFactory {

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
        LoanPolicy basePolicy = new DefaultLoanPolicy();
        LoanPolicy itemAwarePolicy = new ItemTypeLoanPolicyDecorator(basePolicy);
        return new WeekendAdjustmentLoanPolicyDecorator(itemAwarePolicy);
    } // 14 zile (cum ai)

    @Override
    public PenaltyStrategy penaltyStrategy() {
        return new ItemTypePenaltyStrategy();
    }
}
