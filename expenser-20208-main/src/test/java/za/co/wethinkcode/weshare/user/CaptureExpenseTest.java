package za.co.wethinkcode.weshare.user;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class CaptureExpenseTest extends AbstractUserTest {
    @Test
    void captureExpense() {
        // Login
        doLogin("student@wethinkcode.co.za");

        // load the page to capture an expense
        navigateToNewExpensePage();

        // fill form and submit it
        submitExpense("Friday Lunch", "300.00", "17-10-2021");

        // the expenses table should be visible
        assertThat(driver.findElement(By.id("expenses")).isDisplayed()).isTrue();

        // the first entry should be the expense captured
        assertThat(driver.findElement(By.id("claim_expense_1")).getText()).isEqualTo("Friday Lunch");
        assertThat(driver.findElement(By.id("amount_1")).getText()).isEqualTo("300.00");
        assertThat(driver.findElement(By.id("date_1")).getText()).isEqualTo("2021-10-17");

        // check the totals
        assertThat(driver.findElement(By.id("expenses_total")).getText()).isEqualTo("300.00");
        assertThat(driver.findElement(By.id("nett_expenses")).getText()).isEqualTo("300.00");

        // check that nobody owes nor owing anyone
        assertThat(driver.findElement(By.id("nobody_owes")).getText()).isEqualTo("Nobody owes you any money!");
        assertThat(driver.findElement(By.id("owe_nobody")).getText()).isEqualTo("You don't owe anyone!");
    }
}
