package za.co.wethinkcode.weshare.app.db.memory;

import com.google.common.collect.ImmutableList;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MemoryDb implements DataRepository {
    private final Set<Person> people = new HashSet<>();
    private final Set<Expense> expenses = new HashSet<>();
    private final Set<Claim> claims = new HashSet<>();
    private final Set<Settlement> settlements = new HashSet<>();

    volatile long lastPersonId = 0L;

    public MemoryDb() {
        setupTestData();
    }

    //<editor-fold desc="Persons">

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<Person> allPersons() {
        return ImmutableList.copyOf(people);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Person> findPerson(String email) {
        return people.stream()
                .filter(Person -> Person.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Person addPerson(Person person) {
        Optional<Person> alreadyExists = findPerson(person.getEmail());
        if (alreadyExists.isPresent()) {
            return alreadyExists.get();
        }
        person.setId(nextPersonId());
        people.add(person);
        return person;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePerson(Person person) {
        people.remove(person);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePerson(Person updatedPerson) {
        Optional<Person> PersonOpt = people.stream().filter(Person -> Person.getEmail().equalsIgnoreCase(updatedPerson.getEmail())).findFirst();
        if (PersonOpt.isPresent()) {
            people.remove(PersonOpt.get());
            people.add(updatedPerson);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Expenses">

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<Expense> getExpenses(Person belongsTo) {
        return expenses.stream()
                .filter(expense -> expense.getPaidBy().equals(belongsTo))
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Expense> getExpense(UUID id) {
        return expenses.stream()
                .filter(expense -> expense.getId().equals(id))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expense addExpense(Expense expense) {
        expenses.add(expense);
        return expense;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateExpense(Expense updatedExpense) {
        Optional<Expense> ExpenseOpt = expenses.stream().filter(expense -> expense.equals(updatedExpense)).findFirst();
        if (ExpenseOpt.isPresent()) {
            expenses.remove(ExpenseOpt.get());
            expenses.add(updatedExpense);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<Expense> findExpensesPaidBy(Person person) {
        return expenses.stream()
                .filter(expense -> expense.getPaidBy().getEmail().equalsIgnoreCase(person.getEmail()))
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTotalExpensesFor(Person person) {
        return findExpensesPaidBy(person).stream().mapToDouble(Expense::getNettAmount).sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNettExpensesFor(Person person) {
        return getTotalExpensesFor(person)
                - getTotalUnsettledClaimsClaimedBy(person)
                + getTotalUnsettledClaimsClaimedFrom(person);
    }
    //</editor-fold>

    //<editor-fold desc="Claims">

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<Claim> getClaims() {
        return ImmutableList.copyOf(claims);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableList<Claim> getClaimsBy(Person claimedBy, boolean onlyUnsettled) {
        return claims.stream().filter(claim -> claim.getClaimedBy().equals(claimedBy)
                        && (!onlyUnsettled || !claim.isSettled()))
                .sorted(Comparator.comparing(Claim::getDueDate))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Claim> getClaim(UUID id) {
        return claims.stream()
                .filter(Claim -> Claim.getId().equals(id))
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Claim addClaim(Claim claim) {
        claims.add(claim);
        return claim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeClaim(Claim claim) {
        claims.remove(claim);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateClaim(Claim updatedClaim) {
        Optional<Claim> ClaimOpt = claims.stream().filter(Claim -> Claim.equals(updatedClaim)).findFirst();
        if (ClaimOpt.isPresent()) {
            claims.remove(ClaimOpt.get());
            claims.add(updatedClaim);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Claim> findUnsettledClaimsClaimedBy(Person person) {
        return claims.stream().filter(Claim ->
                        Claim.getClaimedBy().getEmail().equalsIgnoreCase(person.getEmail()) &&
                                !Claim.isSettled())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTotalUnsettledClaimsClaimedBy(Person person) {
        return findUnsettledClaimsClaimedBy(person).stream().mapToDouble(Claim::getAmount).sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getTotalUnsettledClaimsClaimedFrom(Person person) {
        return findUnsettledClaimsClaimedFrom(person).stream().mapToDouble(Claim::getAmount).sum();
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ImmutableList<Claim> getClaimsFrom(Person claimedFrom, boolean onlyUnsettled) {
        return claims.stream()
                .filter(claim -> claim.getClaimedFrom().equals(claimedFrom) && (!onlyUnsettled || !claim.isSettled()))
                .sorted(Comparator.comparing(Claim::getDueDate))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ImmutableList<Claim> findUnsettledClaimsClaimedFrom(Person person) {
        return getClaimsFrom(person, true);
    }
    //</editor-fold>

    //<editor-fold desc="Settlements">

    /**
     * {@inheritDoc}
     */
    @Override
    public Settlement addSettlement(Settlement settlement) {
        settlements.add(settlement);
        expenses.add(settlement.getGeneratedExpense());
        return settlement;
    }

    @Override
    public void dropExpenses() {
        expenses.clear();
    }

    @Override
    public void dropClaims() {
        claims.clear();
    }

    @Override
    public void dropSettlements() {
        settlements.clear();
    }
    //</editor-fold>

    //<editor-fold desc="Helpers">

    /**
     * Answer the next available ID for a Person.
     * <p>
     * Incrementing a {@code long} value has to be synchronized because it is not an atomic
     * operation. See the Java Language Specification (§17.7 in Java SE17 Ed.) for details.
     *
     * @return The new ID.
     */
    private long nextPersonId() {
        synchronized (this) {
            return ++lastPersonId;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Test Data">
    private void setupTestData() {
        Person herman = addPerson(new Person("herman@wethinkcode.co.za"));
        Person mike = addPerson(new Person("mike@wethinkcode.co.za"));
        addPerson(new Person("sett@wethinkcode.co.za"));

        /// herman's expenses
        addExpense(new Expense(100.00, LocalDate.of(2021, 10, 12), "Airtime", herman));
        addExpense(new Expense(35.00, LocalDate.of(2021, 10, 15), "Uber", herman));
        Expense braai = addExpense(new Expense(400.00, LocalDate.of(2021, 9, 28), "Braai", herman));

        // herman claims from mike
        addClaim(braai.createClaim(mike, 200.0, LocalDate.of(2021, 11, 1)));

        //mikes expenses
        Expense beers = addExpense(new Expense(420.00, LocalDate.of(2021, 9, 30), "Beers", mike));

        // mike claim from herman
        addClaim(beers.createClaim(herman, 200.00, LocalDate.of(2021, 11, 1)));
    }
    //</editor-fold>
}