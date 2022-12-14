package za.co.wethinkcode.weshare.model;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.TestData;
import za.co.wethinkcode.weshare.app.model.Person;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTests {
    @Test
    public void newPersonDoesNotHaveId() {
        Person p = TestData.me();
        assertNull(p.getId());
    }

    @Test
    public void personWithNoEmailFails() {
        assertThrows(NullPointerException.class, () -> new Person(null));
    }

    @Test
    public void pseudonymFromEmailAddress() {
        Person p = TestData.me();
        assertEquals("Me", p.getName());
    }

    @Test
    public void pseudonymFromNonEmailAddress() {
        Person p = new Person("me, myself and I");
        assertEquals("Me, myself and I", p.getName());
    }
}
