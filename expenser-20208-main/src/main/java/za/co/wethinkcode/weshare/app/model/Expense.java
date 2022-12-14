package za.co.wethinkcode.weshare.app.model;

import com.google.common.base.Strings;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * I record the details of an Expense, created when a person records an actual expense paid by them.
 * The person can then choose to (as a next step) lodge one or more claims from this expense against others.
 *
 * I use Person instances to record who incurred the Expense.
 *
 * Note that my instances are immutable.
 */
// TODO: to think about: does an expense automatically start with a claim from a person to him/herself? or is that unnecessary overhead?
// To keep things simple at the start we are assuming uniqueness based on id.

public class Expense extends AbstractTransactionModelBase {

    private final LocalDate date;
    private final String description;
    private final Person paidBy;
    private final Set<Claim> claims;


    //<editor-fold desc="Lifecycle">

    /**
     * Initialise an Expense instance with all it's needed data.
     * @param amount R value of the expense item
     * @param date Date on which the expense was paid
     * @param description Some descriptive text, possibly null or empty.
     * @param paidBy The Person who paid for the expense item.
     */
    public Expense(double amount, LocalDate date, String description, Person paidBy) {
        super( UUID.randomUUID(), amount );
        this.date = date;
        this.description = Strings.isNullOrEmpty(description) ? "Unspecified" : description;
        this.paidBy = paidBy;
        this.claims = new HashSet<>();
    }
    //</editor-fold>

    //<editor-fold desc="Behaviour">
    public Claim createClaim( Person claimedFrom, Double amount, LocalDate dueDate ){
        Double currentTotalClaimed = this.claims.stream().mapToDouble(Claim::getAmount).sum();
        if (currentTotalClaimed + amount > this.getAmount()) {
            throw new RuntimeException("Total claims exceeds the amount of the expense");
        }
        Claim claim = new Claim( this, claimedFrom, amount, dueDate );
        this.claims.add(claim);
        return claim;
    }

    public void removeClaim(Claim claim){
        this.claims.remove(claim);
    }

    //</editor-fold>

    //<editor-fold desc="Accessors">
    public Set<Claim> getClaims() {
        return claims.stream()
                .sorted(Comparator.comparing(Claim::getDueDate))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public LocalDate getDate() { return date; }

    public String getDescription() {
        return description;
    }

    public Person getPaidBy() {
        return paidBy;
    }

    //</editor-fold>

    //<editor-fold desc="Utilities">

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expense expense = (Expense) o;

        if (!date.equals(expense.date)) return false;
        if (!description.equals(expense.description)) return false;
        return paidBy.equals(expense.paidBy);
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + paidBy.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", paidBy=" + paidBy +
                ", id=" + id +
                ", amount=" + amount +
                '}';
    }

    public String getFormattedAmount(){
        return String.format("R %,.2f", this.amount);
    }

    public Double getTotalClaims() {
        if (claims.isEmpty()) return 0.0;
        return claims.stream().map(AbstractTransactionModelBase::getAmount).reduce(Double::sum).get();
    }

    public Double getUnclaimedAmount() {
        return this.amount - getTotalClaims();
    }

    public Double getNettAmount() {
        return this.amount - getTotalSettledClaims();
    }

    private Double getTotalSettledClaims() {
        if (claims.isEmpty()) return 0.0;
        return claims.stream()
                .filter(Claim::isSettled)
                .mapToDouble(Claim::getAmount).sum();
    }
    //</editor-fold>
}