package za.co.wethinkcode.weshare.user;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class NettExpensesTest extends AbstractUserTest {
    @Test
    void nothingToShow() {
        // Login
        doLogin("student@wethinkcode.co.za");

        // there should be no data
        assertThat(driver.findElement(By.id("no_expenses")).getText()).isEqualTo("You don't have any expenses!");
        assertThat(driver.findElement(By.id("nobody_owes")).getText()).isEqualTo("Nobody owes you any money!");
        assertThat(driver.findElement(By.id("owe_nobody")).getText()).isEqualTo("You don't owe anyone!");
    }

    @Test
    void hasExpensesClaimsAndSettlementsToPay() {
        setupDataForUser();

        doLogin("student1@wethinkcode.co.za");

        // the expenses table should be visible
        assertThat(driver.findElement(By.id("expenses")).isDisplayed()).isTrue();

        // expenses
        verifyExpense(1, "Road trip", "600.00", "2021-11-10");
        verifyExpense(2, "Uber", "100.00", "2021-11-12");

        // check the expense totals
        assertThat(driver.findElement(By.id("expenses_total")).getText()).isEqualTo("700.00");

        // the claims table should be visible
        assertThat(driver.findElement(By.id("they_owe_me")).isDisplayed()).isTrue();

        // the settlements table should be visible
        assertThat(driver.findElement(By.id("i_owe_them")).isDisplayed()).isTrue();

        // expenses
        verifySettlement(1, "Lunch");
        verifySettlement(2, "Movies");

        // check the totals
        assertThat(driver.findElement(By.id("expenses_total")).getText()).isEqualTo("700.00");
        assertThat(driver.findElement(By.id("they_owe_me_total")).getText()).isEqualTo("400.00");
        assertThat(driver.findElement(By.id("i_owe_them_total")).getText()).isEqualTo("200.00");
        assertThat(driver.findElement(By.id("nett_expenses")).getText()).isEqualTo("500.00");

    }

    private void verifySettlement(int row, String description) {
        assertThat(driver.findElement(By.id("settle_" + row)).getText()).isEqualTo(description);
    }

    private void verifyExpense(int row, String description, String amount, String date) {
        assertThat(driver.findElement(By.id("claim_expense_" + row)).getText()).isEqualTo(description);
        assertThat(driver.findElement(By.id("amount_" + row)).getText()).isEqualTo(amount);
        assertThat(driver.findElement(By.id("date_" + row)).getText()).isEqualTo(date);
    }

    private void setupDataForUser() {
        // Three students
        Person student1 = new Person("student1@wethinkcode.co.za");
        DataRepository.getInstance().addPerson(student1);

        Person student2 = new Person("student2@wethinkcode.co.za");
        DataRepository.getInstance().addPerson(student2);

        Person student3 = new Person("student3@wethinkcode.co.za");
        DataRepository.getInstance().addPerson(student2);

        // student 1 has an expense and claims from student 2 and 3
        Expense expense1 = new Expense(600.00, LocalDate.of(2021,11,10), "Road trip", student1);
        DataRepository.getInstance().addExpense(expense1);

        Claim claim1 = expense1.createClaim(student2, 200.00, LocalDate.of(2021,11,20));
        DataRepository.getInstance().addClaim(claim1);

        Expense expense2 = new Expense(100.00, LocalDate.of(2021,11,12), "Uber", student1);
        DataRepository.getInstance().addExpense(expense2);

        Claim claim2 = expense1.createClaim(student3, 200.00, LocalDate.of(2021,11,30));
        DataRepository.getInstance().addClaim(claim2);

        // student 2 has an expense and claims from student 1
        Expense expense3 = new Expense(200.00, LocalDate.of(2021,11,1), "Lunch", student2);
        DataRepository.getInstance().addExpense(expense3);

        Claim claim3 = expense3.createClaim(student1, 100.00, LocalDate.of(2021,11,5));
        DataRepository.getInstance().addClaim(claim3);

        // student 3 has an expense and claims from student 1
        Expense expense4 = new Expense(200.00, LocalDate.of(2021,11,7), "Movies", student3);
        DataRepository.getInstance().addExpense(expense4);

        Claim claim4 = expense4.createClaim(student1, 100.00, LocalDate.of(2021,11,9));
        DataRepository.getInstance().addClaim(claim4);
    }
}
