package za.co.wethinkcode.weshare.user;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class SettleClaimTest extends AbstractUserTest {
    @Test
    void settleClaim() {
        setupExpenseAndClaimToSettle();

        // Student2 logs in and there is a claim to settle
        doLogin("student2@wethinkcode.co.za");

        // student2 has a claims to settle
        student2_shouldHaveClaimToSettle();

        // navigate to settlement page
        student2_navigateToSettlementPage();

        // settle the claim
        student2_settleClaim();

        // check that student has expense and nothing owing
        student2_shouldHaveAnExpenseAndNobodyToOwe();

        // student1 logs in
        doLogin("student1@wethinkcode.co.za");

        // the first entry should be the expense captured
        student1_shouldHaveReducedExpenses();
    }

    private void student2_shouldHaveClaimToSettle() {
        assertThat(driver.findElement(By.id("i_owe_them")).isDisplayed()).isTrue();
    }

    private void student2_settleClaim() {
        driver.findElement(By.id("settle")).submit();
        assertThat(driver.getCurrentUrl()).isEqualToIgnoringCase(appUrl("/home"));
    }

    private void student1_shouldHaveReducedExpenses() {
        assertThat(driver.findElement(By.id("claim_expense_1")).getText()).isEqualTo("Road trip");
        assertThat(driver.findElement(By.id("amount_1")).getText()).isEqualTo("250.00");
        assertThat(driver.findElement(By.id("date_1")).getText())
                .isEqualTo("2021-11-10");

        // check the totals
        assertThat(driver.findElement(By.id("expenses_total")).getText()).isEqualTo("250.00");
        assertThat(driver.findElement(By.id("nett_expenses")).getText()).isEqualTo("250.00");

        // check that nobody owes nor owing anyone
        assertThat(driver.findElement(By.id("nobody_owes")).getText()).isEqualTo("Nobody owes you any money!");
        assertThat(driver.findElement(By.id("owe_nobody")).getText()).isEqualTo("You don't owe anyone!");
    }

    private void student2_shouldHaveAnExpenseAndNobodyToOwe() {
        // nobody to owe
        assertThat(driver.findElement(By.id("owe_nobody")).getText()).isEqualTo("You don't owe anyone!");

        // check expenses
        assertThat(driver.findElement(By.id("claim_expense_1")).getText()).isEqualTo("Road trip");
        assertThat(driver.findElement(By.id("amount_1")).getText()).isEqualTo("250.00");
        assertThat(driver.findElement(By.id("date_1")).getText())
                .isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_DATE));

        // check the home page totals
        assertThat(driver.findElement(By.id("expenses_total")).getText()).isEqualTo("250.00");
        assertThat(driver.findElement(By.id("nett_expenses")).getText()).isEqualTo("250.00");
    }

    private void student2_navigateToSettlementPage() {
        WebElement claimLink = driver.findElement(By.id("settle_1"));
        String href = claimLink.getAttribute("href");
        claimLink.click();
        assertThat(driver.getCurrentUrl()).isEqualToIgnoringCase(href);
        assertThat(driver.findElement(By.id("email")).getAttribute("value")).isEqualTo("student1@wethinkcode.co.za");
        assertThat(driver.findElement(By.id("due_date")).getAttribute("value")).isEqualTo("2021-11-30");
        assertThat(driver.findElement(By.id("description")).getAttribute("value")).isEqualTo("Road trip");
        assertThat(driver.findElement(By.id("claim_amount")).getAttribute("value")).isEqualTo("250.00");
    }

    private void setupExpenseAndClaimToSettle() {
        Person student1 = new Person("student1@wethinkcode.co.za");
        DataRepository.getInstance().addPerson(student1);

        Person student2 = new Person("student2@wethinkcode.co.za");
        DataRepository.getInstance().addPerson(student2);

        Expense expense = new Expense(500.00, LocalDate.of(2021,11,10), "Road trip", student1);
        DataRepository.getInstance().addExpense(expense);

        Claim claimFromStudent2 = expense.createClaim(student2, 250.00, LocalDate.of(2021,11,30));
        DataRepository.getInstance().addClaim(claimFromStudent2);
    }

}
