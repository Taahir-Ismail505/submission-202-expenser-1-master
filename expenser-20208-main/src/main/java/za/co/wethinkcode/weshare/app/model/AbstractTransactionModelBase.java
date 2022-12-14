package za.co.wethinkcode.weshare.app.model;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class AbstractTransactionModelBase {

    protected final UUID id;
    protected final Double amount;

    public AbstractTransactionModelBase( UUID originalExpense, Double amount ){
        this.id = checkNotNull( originalExpense );
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }
}