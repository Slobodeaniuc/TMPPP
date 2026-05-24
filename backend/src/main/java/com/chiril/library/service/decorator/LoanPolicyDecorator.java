package com.chiril.library.service.decorator;

import com.chiril.library.service.LoanPolicy;

public abstract class LoanPolicyDecorator implements LoanPolicy {
    protected final LoanPolicy delegate;

    protected LoanPolicyDecorator(LoanPolicy delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate null");
        }
        this.delegate = delegate;
    }
}
