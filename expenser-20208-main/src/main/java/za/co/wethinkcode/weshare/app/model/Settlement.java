package za.co.wethinkcode.weshare.app.model;

import java.time.LocalDate;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class Settlement extends AbstractTransactionModelBase {

    private final Expense generatedExpense;
    private final Claim claim;
    private final LocalDate settlementDate;

    Settlement( Claim claim, LocalDate settlementDate ){
        super( UUID.randomUUID(), claim.getAmount() );
        this.claim = claim;
        this.generatedExpense = new Expense(claim.getAmount(), settlementDate, claim.getExpense().getDescription(), claim.getClaimedFrom());
        this.settlementDate = checkNotNull( settlementDate );
    }

    public Expense getGeneratedExpense() {
        return generatedExpense;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public Claim getClaim() {
        return claim;
    }
}