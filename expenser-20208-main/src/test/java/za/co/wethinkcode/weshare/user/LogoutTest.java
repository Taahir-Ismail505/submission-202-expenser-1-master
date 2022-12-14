package za.co.wethinkcode.weshare.user;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

public class LogoutTest extends AbstractUserTest {
    @Test
    void logout() {
        doLogin("mike@wethinkcode.co.za");

        driver.findElement(By.id("logout")).click();
        assertThat(driver.getCurrentUrl()).isEqualToIgnoringCase(appUrl("/index.html"));
    }
}
