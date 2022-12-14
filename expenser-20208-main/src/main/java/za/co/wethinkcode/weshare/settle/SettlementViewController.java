package za.co.wethinkcode.weshare.settle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import io.javalin.http.Context;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.nettexpenses.NettExpensesController;

import javax.xml.crypto.Data;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for handling calls from form submits for Claims
 */
public class SettlementViewController {
    public static final String PATH = "/settleclaim";

    public static void renderSettleClaimForm(Context context){
        String queryId = String.valueOf(context.queryParam("claimId"));
        UUID id = UUID.fromString(queryId);
        Optional<Claim> chosenClaim = DataRepository.getInstance().getClaim(id);

        if (chosenClaim.isPresent()) {
            Map<String, Object> viewModel = Map.of(
                    "email", chosenClaim.get().getClaimedBy().getEmail(),
                    "due_date", chosenClaim.get().getDueDate(),
                    "description", chosenClaim.get().getDescription(),
                    "claim_amount", chosenClaim.get().getAmount()

            );
            System.out.println(viewModel.get("claim_amount/-e"));
            context.sessionAttribute("claim", chosenClaim.get());
            context.render("settleclaim.html", viewModel);
        }
    }

    public static void submitSettlement(Context context) {
        Claim claim = context.sessionAttribute("claim");

        if (claim != null) {
            Object object = context.sessionAttribute("user");
            Person person = (Person) object;
            DataRepository dataRepository = DataRepository.getInstance();

            dataRepository.addExpense(new Expense(
                    claim.getAmount(),
                    getCurrentDate(),
                    claim.getExpense().getDescription(),
                    person
            ));

            claim.settleClaim(getCurrentDate());
            DataRepository.getInstance().removeClaim(claim);
        }

        context.redirect(NettExpensesController.PATH);
    }

    private static LocalDate getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();

        return LocalDate.parse(now.toString().substring(0,10), formatter);
    }

}