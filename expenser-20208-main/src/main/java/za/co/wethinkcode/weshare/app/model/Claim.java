package za.co.wethinkcode.weshare.app.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * I record the details of a claim of an expense against a person.
 * The claim might be for smaller amount than the expense amount, if split between people.
 * <p>
 * If a person logs an expense against themselves, then that expense is in confirmed state,
 * and a Claim needs to be created where `claimedBy` and `claimedFrom` is the same person.
 * <p>
 * Note that my instances are immutable.
 */

// For now we use (originalExpense, claimedBy, claimedFrom) as unique fields.
// TODO: also introduce ID rather for Claim?

public class Claim extends AbstractTransactionModelBase {

    private final Person claimedBy;
    private final Person claimedFrom;
    private final LocalDate dueDate;
    private final Expense expense;
    private boolean settled;

    // FIXME: Constructor should not be public
    public Claim(Expense originalExpense, Person claimedFrom, Double amount, LocalDate dueDate) {
        super(UUID.randomUUID(), amount);
        this.claimedBy = originalExpense.getPaidBy();
        this.claimedFrom = claimedFrom;
        this.dueDate = dueDate;
        this.expense = originalExpense;
        this.settled = false;
    }

    public Expense getExpense() {
        return expense;
    }

    public Person getClaimedBy() {
        return claimedBy;
    }

    public String _getClaimedFrom() {
        return claimedFrom.getEmail();
    }

    public Person getClaimedFrom() {
        return claimedFrom;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Settlement settleClaim(LocalDate settlementDate) {
        settled = true;
        return new Settlement(this, settlementDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Claim claim = (Claim) o;
        return getId().equals(claim.getId()) && claimedBy.equals(claim.claimedBy) && claimedFrom.equals(claim.claimedFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), claimedBy, claimedFrom);
    }

    @Override
    public String toString() {
        return "Claim{" +
                "originalExpense=" + getId() +
                ", claimedBy='" + claimedBy + '\'' +
                ", claimedFrom='" + claimedFrom + '\'' +
                ", amount=" + getAmount() +
                ", dueDate=" + dueDate +
                '}';
    }

    public boolean isSettled() {
        return settled;
    }

    public String getDescription() {
        return expense.getDescription();
    }
}