package za.co.wethinkcode.weshare.app.db;

import com.google.common.collect.ImmutableList;
import za.co.wethinkcode.weshare.app.db.memory.MemoryDb;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DataRepository:
 * I define the protocol -- the set of methods -- that other parts of the application can use to
 * persist instances of the domain model (Persons, Expenses, Claims and Settlements).
 * <p>
 * My name, {@code DataRepository}, ins intended to indicate the abstract idea that <em>this</em>
 * is where data lives, without binding to any specific implementation like memory, file or database.
 * <p>
 * For the purposes of our Iteration 2 "WeShare" exercise, a simple, memory-only implementation is
 * provided in {@link MemoryDb}, accessible by calling
 * {@code DataRepository.getInstance()}.
 */
public interface DataRepository {

    DataRepository INSTANCE = new MemoryDb();

    static DataRepository getInstance(){
        return INSTANCE;
    }

    //<editor-fold desc="Persons">
    /**
     * Add a new Person instance to the datastore. If the Person passed to this method already exists
     * in the repository (they have the same ID), we'll return that instance and ignore the request.
     * If you ask for a {@literal null} Person, I will throw a NullPointerException.
     * @param person A non-null Person instance.
     * @return The Person instance you added or that already existed in the db.
     */
    Person addPerson(Person person);

    /**
     * Remove a Person from the database. If you request removal of a Person that is not actually
     * in the database, your request will silently be ignored.
     * @param person A non-null Person instance which will be removed from the db.
     */
    void removePerson(Person person );

    /**
     * Update the given Person instance.
     * @param updatedPerson A non-null Person instance which will be updated.
     */
    void updatePerson(Person updatedPerson );

    /**
     * Find a Person instance with the given email address.
     * @param email Email id of the Person we want to find.
     * @return An Optional containing the Person if they exist in the db, empty otherwise.
     */
    Optional<Person> findPerson( String email );

    /**
     * Answer with a Set of all Person instances in the db.
     * @return A set of Person instances, possible empty, but never {@literal null}.
     */
    ImmutableList<Person> allPersons();
    //</editor-fold>

    //<editor-fold desc="Expenses">
    /**
     * Answer with an ImmutableList of all the Expenses that belong to a Person identified by their email.
     * @param belongsTo A non-null, non-empty email id of a Person.
     * @return A non-null List of Expenses, possibly empty.
     */
    ImmutableList<Expense> getExpenses( Person belongsTo );

    /**
     * Answer all the Expenses paid by a given Person. Expenses (if there's more than one) will be returned
     * in date order from earliest to most recent.
     * @param person The Person whose Expenses we want. May not be {@literal null}.
     * @return A non-null (but possibly empty) Set of Expenses.
     */
    ImmutableList<Expense> findExpensesPaidBy( Person person);

    /**
     * Answer the total amount of all Expenses paid by a given Person.
     * @param person The Person whose Expense-total we want. May not be {@literal null}.
     * @return The total of all expenses paid by the given Person.
     */
    double getTotalExpensesFor(Person person);

    /**
     * Answer the total amount of all Expenses paid by a given Person, PLUS the value of all Claims
     * owed by them, MINUS the value of all Claims owed to them.
     * @param person A non-null Person.
     * @return The total value of the Person's expenses
     */
    double getNettExpensesFor( Person person);

    /**
     * Answer an Optional containing the Expense instance identified by a given UUID. The Optional will be
     * empty if no Expense exists in the db with the given UUID. If you give me a {@literal null} UUID, I
     * will puke a NullPointerException at you.
     * @param id A non-null UUID.
     * @return An Optional containing the corresponding Expense if it exists, empty otherwise.
     */
    Optional<Expense> getExpense( UUID id );

    /**
     * Add the given Expense to the db. If the Expense already exists in the db
     * (as determined by Expense.equals()) I will simply return that instance. If you give me a
     * {@literal null} Expense, I will throw a NullPointerException.
     * @param expense A non-null Expense instance.
     * @return The given Expense or its clone that already exists in the db.
     */
    Expense addExpense(Expense expense );

    /**
     * Remove the given Expense from the db. If the given Expense doesn't exist in the db or you hand me
     * a {@literal null} Expense, I will silently ignore your request.
     * @param expense An Expense to remove. May be {@literal null}.
     */
    void removeExpense(Expense expense );

    /**
     * Update the stored attributes of an Expense in the db. If the Expense does not exist in the db, I
     * will silently ignore your request. If you give me a {@literal null} Expense I will throw a
     * NullPointerException at you.
     * @param updatedExpense A non-null Expense.
     */
    void updateExpense(Expense updatedExpense );
    //</editor-fold>

    //<editor-fold desc="Claims">
    /** Get a Claim given its id. If no Claim exists in the db with the given id, we'll answer
     * with an empty Optional.
     * @param id The (non-null) id of a Claim.
     * @return An Optional containing the Claim, or empty if it does not exist in the db.
     */
    Optional<Claim> getClaim(UUID id);

    /**
     * Answer a Set containing all Claims.
     * @return A non-null, but maybe empty, ImmutableSet of all Claims in the db.
     */
    ImmutableList<Claim> getClaims();

    /**
     * Answer a List of all Claims made by a Person, identified by their email id.
     * @param claimedBy The email id of the Person whose claims you're looking for.
     * @param onlyUnsettled if true, only return the claims which are not yet settled
     * @return A non-null ImmutableList which may be empty.
     */
    ImmutableList<Claim> getClaimsBy( Person claimedBy, boolean onlyUnsettled );

    /**
     * Answer a List of Claims FROM a Person, identified by their email id.
     * @param claimedFrom A non-null, non-empty email id.
     * @param onlyUnsettled if true, only return the claims which are not yet settled
     * @return An ImmutableList of Claims, possible empty.
     */
    ImmutableList<Claim> getClaimsFrom( Person claimedFrom, boolean onlyUnsettled );

    /**
     * Answer the Set of all unsettled Claims claimed FROM a given Person.
     * @param person The Person whose unsettled Claims we want. May not be {@literal null}.
     * @return A non-null (but possibly empty) Set of Claims.
     */
    //FIXME: duplicates getClaimsFrom
    ImmutableList<Claim> findUnsettledClaimsClaimedFrom(Person person);

    /**
     * Answer the (money) total of all unsettled Claims claimed FROM a given Person.
     * @param person A Person who owes zero or more Claims. May not be {@literal null}.
     * @return The total of all Claim amounts owed by the given Person.
     */
    double getTotalUnsettledClaimsClaimedFrom(Person person);

    /**
     * Answer the Set of all unsettled Claims owed BY a given Person.
     * @param person The Person whose unsettled Claims we want. May not be {@literal null}.
     * @return A non-null (but possibly empty) Set of Claims.
     */
    List<Claim> findUnsettledClaimsClaimedBy(Person person);

    /**
     * Answer the (money) total of all unsettled Claims claimed BY a given Person.
     * @param person A Person who owes zero or more Claims. May not be {@literal null}.
     * @return The total of all Claim amounts owed by the given Person.
     */
    double getTotalUnsettledClaimsClaimedBy( Person person);

    /**
     * Add a given Claim instance to the db. If it already exists (as determined by Claim::equals),
     * I will ignore you and return the existing Claim. If you give me a {@literal null} Claim I will
     * throw my NullPointerException toys.
     * @param claim A non-null Claim object.
     * @return The Claim added to the db or that was already there.
     */
    Claim addClaim(Claim claim );

    /**
     * Delete a given Claim from the db if it exists. If you give me a {@literal null} Claim I will throw a
     * NullPointerException at you.
     * @param claim A non-null Claim. I will silently ignore a {@literal null} Claim.
     */
    void removeClaim(Claim claim );

    /**
     * Update the fields of a Claim. If the Claim does not exist in the db I will silently ignore you. If you give
     * me a {@literal null} Claim I will throw a NullPointerException.
     * @param updatedClaim A non-null Claim.
     */
    void updateClaim(Claim updatedClaim );
    //</editor-fold>

    //<editor-fold desc="Settlements">
    /**
     * Store a given Settlement in the db if it's not already in there. If it is I will ignore your
     * request, and if you give me a {@literal null} Settlement I will throw a NullPointerException.
     * @param settleClaim A non-null Settlement instance.
     * @return The same Settlement object after it's been persisted.
     */
    Settlement addSettlement(Settlement settleClaim);
    //</editor-fold>

    //<editor-fold desc="Database operations">
    /**
     * Drop all expenses without any check
     * WARNING! This can cause data integrity violations!!!
     */
    void dropExpenses();

    /**
     * Drop all claims without any check
     * WARNING! This can cause data integrity violations!!!
     */
    void dropClaims();

    /**
     * Drop all settlements without any check
     * WARNING! This can cause data integrity violations!!!
     */
    void dropSettlements();

    //</editor-fold>
}