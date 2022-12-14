package za.co.wethinkcode.weshare.model;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.TestData;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SettlementTests {

    @Test
    public void settleClaim() {
        Expense e = TestData.lunchExpense(100.00);
        Claim c = e.createClaim(TestData.friend("friend"), 50.00, LocalDate.of(2021, 10, 31));
        Settlement s = c.settleClaim(LocalDate.of(2021, 10, 31));
        assertTrue(c.isSettled());
        assertEquals(c, s.getClaim());
        assertEquals( c.getAmount(), s.getGeneratedExpense().getAmount());
        assertEquals( c.getExpense().getDescription(), s.getGeneratedExpense().getDescription());
        assertEquals(s.getSettlementDate(), s.getGeneratedExpense().getDate());
        assertEquals(c.getClaimedFrom(), s.getGeneratedExpense().getPaidBy());
        assertEquals(50.00, e.getNettAmount());
    }
}
