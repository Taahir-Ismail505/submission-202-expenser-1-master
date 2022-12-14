package za.co.wethinkcode.weshare.nettexpenses;

import com.google.common.collect.ImmutableList;
import io.javalin.http.Context;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.expense.CaptureExpenseController;

import java.util.Map;

public class NettExpensesController {
    public static final String PATH = "/home";

    public static void renderHomePage(Context context) {
        Person person = context.sessionAttribute("user");
        if (person == null) {
            throw new InternalServerErrorResponse("Could not retrieve user's details.");
        }

        ImmutableList<Expense> expenses = DataRepository.getInstance().getExpenses(person);
        ImmutableList<Claim> claimsTo = DataRepository.getInstance().getClaimsFrom(person, true);
        ImmutableList<Claim> claimsFrom = DataRepository.getInstance().getClaimsBy(person, true);

        Double totalAmountExpenses = getTotalExpenses(expenses);
        Double totalAmountClaimsTo = getTotalClaims(claimsTo);
        Double totalAmountClaimsFrom = getTotalClaims(claimsFrom);

        context.sessionAttribute("numberOfExpenses", expenses.size());
        context.sessionAttribute("numberOfClaimsTo", claimsTo.size());
        context.sessionAttribute("numberOfClaimsFrom", claimsFrom.size());

        context.sessionAttribute("totalAmountExpenses", totalAmountExpenses);
        context.sessionAttribute("totalAmountClaimsTo", totalAmountClaimsTo);
        context.sessionAttribute("totalAmountClaimsFrom", totalAmountClaimsFrom);

        context.sessionAttribute(
                "nettExpenses",
                getNettExpense(totalAmountExpenses, totalAmountClaimsTo, totalAmountClaimsFrom)
        );

        Map<String, Object> viewModel = Map.of("expenses", expenses, "claimsFrom", claimsFrom, "claimsTo", claimsTo);
        context.render("home.html", viewModel);
    }

    private static Double getNettExpense(Double totalExpenses, Double totalClaimsTo, Double totalClaimsFrom) {
        Double result = (totalExpenses + totalClaimsTo) - totalClaimsFrom;
        return result;
    }

    private static Double getTotalExpenses(ImmutableList<Expense> expenses) {
        Double total = 0.0;

        for (Expense expense : expenses) {
            total += expense.getAmount();
        }

        return total;
    }

    private static Double getTotalClaims(ImmutableList<Claim> claims) {
        Double total = 0.0;

        for (Claim claim : claims) {
            total += claim.getAmount();
        }

        return total;
    }
}