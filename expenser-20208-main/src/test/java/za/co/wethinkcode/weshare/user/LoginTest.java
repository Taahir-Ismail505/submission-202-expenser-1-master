package za.co.wethinkcode.weshare.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends AbstractUserTest {
    @Test
    void successfulLogin() {
        doLogin("student@wethinkcode.co.za");
    }

    @Test
    void redirectWhenNotLoggedIn() {
        navigateTo("/home");
        assertThat(driver.getCurrentUrl()).isEqualToIgnoringCase(appUrl("/index.html"));
    }
}
