package za.co.wethinkcode.weshare.claim;

import io.javalin.http.Context;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;

import java.util.*;

/**
 * Controller for handling calls from form submits for Claims
 */
public class ClaimExpenseController {

    public static final String PATH = "/claimexpense";

    public static void renderClaimExpensePage(Context context) {
        Map<String, Object> viewModel = Map.of();

        String id = context.queryParam("claimId");
        UUID uuid = UUID.fromString(id);
        Expense expense = DataRepository.getInstance().getExpense(uuid).get();

        context.sessionAttribute("expense", expense);
        ArrayList<Claim> claims = new ArrayList<>(DataRepository.getInstance().getExpense(uuid).get().getClaims());
        context.sessionAttribute("claims", claims);
        int claimsIndex = claims.size();
//        System.out.println(claimsIndex);
        context.sessionAttribute("claimsIndex", claimsIndex);
//        viewModel = Map.of("claims", claims);

        context.render("claimexpense.html", viewModel);
    }
}
