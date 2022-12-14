package za.co.wethinkcode.weshare.user;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class ClaimExpenseTest extends AbstractUserTest {
    @Test
    void claimExpenseOnce() {
        // Login
        doLogin("student@wethinkcode.co.za");

        // capture an expense
        navigateToNewExpensePage();
        submitExpense("Friday Lunch", "300.00", "17-10-2021");

        // load page to claim an expense
        navigateToClaimExpensePage();

        // capture a claim
        submitClaim("bestie@wethinkcode.co.za", "50", "30-10-2021");

        // go back home and reload the claim page
        navigateTo("/home");
        navigateToClaimExpensePage();

        // the first entry should be the expense captured
        verifyClaim(1, "Bestie", "50.00", "2021-10-30");

        assertThat(driver.findElement(By.id("total_claims")).getText()).isEqualTo("50.00");
        assertThat(driver.findElement(By.id("unclaimed_amount")).getText()).isEqualTo("250.00");
    }

    @Test
    void claimExpenseTwice() {
        // Login
        doLogin("student@wethinkcode.co.za");

        // capture an expense
        navigateToNewExpensePage();
        submitExpense("Friday Lunch", "300.00", "17-10-2021");

        // load page to claim an expense
        navigateToClaimExpensePage();

        // capture a claim
        submitClaim("mickey@wethinkcode.co.za", "50", "30-10-2021");
        submitClaim("minnie@wethinkcode.co.za", "150", "31-10-2021");

        // go back home and reload the claim page
        navigateTo("/home");
        navigateToClaimExpensePage();

        // the first entry should be the expense captured
        verifyClaim(1, "Mickey", "50.00", "2021-10-30");
        verifyClaim(2, "Minnie", "150.00", "2021-10-31");

        assertThat(driver.findElement(By.id("total_claims")).getText()).isEqualTo("200.00");
        assertThat(driver.findElement(By.id("unclaimed_amount")).getText()).isEqualTo("100.00");
    }

    private void verifyClaim(int row, String name, String amount, String date) {
        assertThat(driver.findElement(By.id("claim_who_" + row)).getText()).isEqualTo(name);
        assertThat(driver.findElement(By.id("claim_amount_" + row)).getText()).isEqualTo(amount);
        assertThat(driver.findElement(By.id("claim_settled_" + row)).getText()).isEqualTo("No");
        assertThat(driver.findElement(By.id("claim_date_" + row)).getText()).isEqualTo(date);
    }

    protected void submitClaim(String email, String amount, String date) {
        fillField("email", email);
        fillField("claim_amount", amount);
        fillField("due_date", date);

        WebElement addClaim = driver.findElement(By.id("add_claim"));
        addClaim.click();
    }

    protected void navigateToClaimExpensePage() {
        WebElement claimLink = driver.findElement(By.id("claim_expense_1"));
        String href = claimLink.getAttribute("href");
        claimLink.click();
        assertThat(driver.getCurrentUrl()).isEqualToIgnoringCase(href);
        assertThat(driver.findElement(By.id("expense_date")).getText()).isEqualTo("2021-10-17");
        assertThat(driver.findElement(By.id("expense_description")).getText()).isEqualTo("Friday Lunch");
        assertThat(driver.findElement(By.id("expense_amount")).getText()).isEqualTo("300.00");
    }}
