package com.pie.demo.enums;

import java.util.EnumSet;
import java.util.Set;

public enum LoanState {
    RESERVED,
    BORROWED,
    RETURNED,
    CANCELLED,
    EXPIRED;

    public static final Set<LoanState> ACTIVE = EnumSet.of(RESERVED, BORROWED);

    public boolean isActive() {
        return ACTIVE.contains(this);
    }

    public boolean canTransitionTo(LoanState next) {
        return switch (this) {
            case RESERVED -> next == BORROWED || next == CANCELLED || next == EXPIRED;
            case BORROWED -> next == RETURNED;
            case RETURNED, CANCELLED, EXPIRED -> false;
        };
    }
}
