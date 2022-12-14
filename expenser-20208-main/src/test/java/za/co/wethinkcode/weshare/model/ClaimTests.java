package za.co.wethinkcode.weshare.model;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.TestData;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClaimTests {
    @Test
    void claimOnceAgainstExpense() {
        Expense e = TestData.lunchExpense(100.00);
        Claim c = e.createClaim(TestData.friend("friend"), 50.00, LocalDate.of(2021, 10, 31));
        assertEquals(1, e.getClaims().size());
        assertEquals(e.getPaidBy(), c.getClaimedBy());
        assertEquals(e, c.getExpense());
        assertEquals(e.getDescription(), c.getDescription());
        assertEquals(50.00, e.getUnclaimedAmount());
    }

    @Test
    void claimTwiceAgainstExpense() {
        Expense e = TestData.lunchExpense(100.00);
        e.createClaim(TestData.friend("friend1"), 50.00, LocalDate.of(2021, 10, 31));
        e.createClaim(TestData.friend("friend2"), 50.00, LocalDate.of(2021, 10, 30));
        assertEquals(2, e.getClaims().size());
        assertEquals(0.00, e.getUnclaimedAmount());
    }

    @Test
    public void cannotClaimMoreThanTheExpense() {
        Expense e = TestData.lunchExpense(100);
        assertThrows(RuntimeException.class, () ->
                e.createClaim(TestData.friend("friend"), 200.00, LocalDate.of(2021,10,31)));
    }
}
