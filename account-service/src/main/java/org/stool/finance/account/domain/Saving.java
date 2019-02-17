package org.stool.finance.account.domain;

import java.math.BigDecimal;

public class Saving {

    private long id;
    private BigDecimal amount;
    private BigDecimal interest;
    private boolean deposit;
    private boolean capitalization;
    private long accountId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public boolean isDeposit() {
        return deposit;
    }

    public void setDeposit(boolean deposit) {
        this.deposit = deposit;
    }

    public boolean isCapitalization() {
        return capitalization;
    }

    public void setCapitalization(boolean capitalization) {
        this.capitalization = capitalization;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
