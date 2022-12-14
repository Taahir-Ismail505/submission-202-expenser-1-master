package za.co.wethinkcode.weshare;

import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.db.memory.MemoryDb;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.LocalDate;

public class TestData {

    public static Person me() {
        return new Person("me@mydomain.com");
    }

    public static Expense lunchExpense(double amount) {
        return new Expense(amount, LocalDate.of(2021, 10, 20), "Lunch", me());
    }

    public static Person friend(String name) {
        return new Person(name + "@friends.com");
    }

    public static DataRepository dbWithNoExpenses() {
        DataRepository db = new MemoryDb();
        db.addPerson(new Person("herman@wethinkcode.co.za"));
        return db;
    }

    public static DataRepository dbWithExpensesAndClaimsForAndAgainst() {
        DataRepository db = new MemoryDb();

        Person herman = db.addPerson(new Person("herman@wethinkcode.co.za"));
        Person mike = db.addPerson(new Person("mike@wethinkcode.co.za"));
        db.addPerson(new Person("sett@wethinkcode.co.za"));

        /// herman's expenses
        db.addExpense(new Expense(100.00, LocalDate.of(2021, 10, 12), "Airtime", herman));
        db.addExpense(new Expense(35.00, LocalDate.of(2021, 10, 15), "Uber", herman));
        Expense braai = db.addExpense(new Expense(400.00, LocalDate.of(2021, 9, 28), "Braai", herman));

        // herman claims from mike
        db.addClaim(new Claim(braai, mike, 200.00, LocalDate.of(2021, 11, 1)));

        return db;
    }
}