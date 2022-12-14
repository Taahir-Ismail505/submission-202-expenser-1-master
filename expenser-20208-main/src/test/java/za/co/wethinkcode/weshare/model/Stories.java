package za.co.wethinkcode.weshare.model;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.TestData;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class Stories {

    @Test
    public void HermanCapturesHisFirstExpense() {
        DataRepository db = TestData.dbWithNoExpenses();

        // Herman is a user
        Person herman = db.findPerson("herman@wethinkcode.co.za")
                .orElseThrow(() -> new RuntimeException("herman not found"));

        // Herman is recording an expense for lunch
        Expense fridayLunch = db.addExpense(new Expense(300.00, LocalDate.of(2021, 10, 23), "Friday lunch", herman));
        assertNotNull(fridayLunch);
        assertEquals(0, fridayLunch.getClaims().size());

        // There should be a lunch expense for herman
        List<Expense> hermansExpenses = db.findExpensesPaidBy(herman);
        Optional<Expense> maybeExpense = hermansExpenses.stream().findFirst();
        assertTrue(maybeExpense.isPresent());
        Expense expense = maybeExpense.get();
        assertEquals(400.00, expense.getAmount());
        assertEquals(LocalDate.of(2021, 9, 28), expense.getDate());
        assertEquals("Braai", expense.getDescription());
        assertEquals(herman, expense.getPaidBy());

        // Total expenses should be correct too
        Double totalExpenses = hermansExpenses.stream().mapToDouble(Expense::getAmount).sum();
        assertEquals(835.00, totalExpenses);
        assertEquals(totalExpenses, db.getTotalExpensesFor(herman));
    }

    @Test
    public void HermanCapturesMoreExpenses() {
        DataRepository db = TestData.dbWithExpensesAndClaimsForAndAgainst();

        // Herman is a user
        Person herman = db.findPerson("herman@wethinkcode.co.za")
                .orElseThrow(() -> new RuntimeException("herman not found"));

        // Herman already has a few expenses
        List<Expense> hermansExpenses = db.findExpensesPaidBy(herman);
        double previousTotalExpenses = db.getTotalExpensesFor(herman);
        int numberOfExpenses = hermansExpenses.size();

        // Remember nett expenses before the new expense
        double previousNettExpenses = db.getNettExpensesFor(herman);

        // Herman is recording an expense for lunch
        db.addExpense(new Expense(300.00, LocalDate.of(2021, 10, 23), "Friday lunch", herman));

        // There should be another expense for Herman
        hermansExpenses = db.findExpensesPaidBy(herman);
        assertEquals(numberOfExpenses + 1, hermansExpenses.size());

        // Total expenses should be correct too
        assertEquals(previousTotalExpenses + 300.00, db.getTotalExpensesFor(herman));

        // Nett Expenses should be correct too
        assertEquals(previousNettExpenses + 300.00, db.getNettExpensesFor(herman));
    }

    @Test
    public void HermanClaimsAnExpenseFromSettAndMike() {
        DataRepository db = TestData.dbWithExpensesAndClaimsForAndAgainst();

        // Herman is a user
        Person herman = db.findPerson("herman@wethinkcode.co.za")
                .orElseThrow(() -> new RuntimeException("herman not found"));

        // Herman already has claims against some people
        List<Claim> hermansClaims = db.findUnsettledClaimsClaimedBy(herman);
        Double previousTotalClaims =  db.getTotalUnsettledClaimsClaimedBy(herman);
        int numberOfClaims = hermansClaims.size();

        // Herman has an expense for lunch
        Expense fridayLunch = db.addExpense(new Expense(300.00, LocalDate.of(2021, 10, 23), "Friday lunch", herman));

        // Remember nett expenses before the new expense
        Double previousNettExpenses = db.getNettExpensesFor(herman);

        // Herman makes a claim for lunch against Sett
        Person sett = db.findPerson("sett@wethinkcode.co.za")
                .orElseThrow(() -> new RuntimeException("sett not found"));
        Claim claimForSett = db.addClaim(fridayLunch.createClaim(sett, 50.00, LocalDate.of(2021, 10, 30)));

        // Herman makes a claim for lunch against Mike
        Person mike = db.findPerson("mike@wethinkcode.co.za")
                .orElseThrow(() -> new RuntimeException("mike not found"));
        Claim claimForMike = db.addClaim(fridayLunch.createClaim(mike, 150.00, LocalDate.of(2021, 10, 30)));

        // Herman should have 2 more claims
        assertEquals(previousTotalClaims + claimForSett.getAmount() + claimForMike.getAmount(),
                db.getTotalUnsettledClaimsClaimedBy(herman));
        hermansClaims = db.findUnsettledClaimsClaimedBy(herman);
        assertEquals(numberOfClaims + 2, hermansClaims.size());

        // Nett Expenses should be correct too
        assertEquals(previousNettExpenses - claimForSett.getAmount() - claimForMike.getAmount(),
                db.getNettExpensesFor(herman));
    }

    @Test
    public void HermanSettlesAClaimFromSett() {
        DataRepository db = TestData.dbWithExpensesAndClaimsForAndAgainst();

        // Herman is a user
        Person herman = db.findPerson("herman@wethinkcode.co.za")
                .orElseThrow(() -> new RuntimeException("herman not found"));
        // and so is Sett
        Person sett = db.findPerson("sett@wethinkcode.co.za")
                .orElseThrow(() -> new RuntimeException("sett not found"));

        // Sett has an expense and he claims it from Herman
        Expense settsExpense = db.addExpense(new Expense(200.00, LocalDate.of(2021, 9, 17), "Movies", sett));
        Claim settsClaim = db.addClaim(settsExpense.createClaim(herman, 100.00, LocalDate.of(2021,10,17)));

        // Remember Hermans expenses and nett
        List<Expense> hermansExpenses = db.findExpensesPaidBy(herman);
        Double totalExpenses = db.getTotalExpensesFor(herman);
        int numberOfExpenses = hermansExpenses.size();
        Double nettExpenses = db.getNettExpensesFor(herman);

        // find the claim from Sett that Herman has to settle
        Claim claimFromSett = db.getClaim(settsClaim.getId())
                .orElseThrow(() -> new RuntimeException("claim not found"));

        // Herman settles Sett's claim
        Settlement settlement = db.addSettlement(claimFromSett.settleClaim(LocalDate.of(2021,10,30)));

        // Herman should have 1 more expense
        assertEquals(numberOfExpenses + 1, db.findExpensesPaidBy(herman).size());
        assertEquals(totalExpenses + settlement.getAmount(), db.getTotalExpensesFor(herman));

        // Nett Expenses should be also by updated
        assertEquals(nettExpenses, db.getNettExpensesFor(herman));
    }
}