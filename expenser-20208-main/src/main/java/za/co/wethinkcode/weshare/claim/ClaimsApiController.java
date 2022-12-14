package za.co.wethinkcode.weshare.claim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller for handling API calls for Claims
 */
public class ClaimsApiController {
    public static final String PATH = "/api/claims";

    public static void create(Context context) throws JsonProcessingException {
        Expense expense = context.sessionAttribute("expense");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObject = mapper.readTree(context.body());

        String email = String.valueOf(jsonObject.get("email"));

        Double amount = Double.parseDouble(jsonObject.get("claim_amount").asText());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = jsonObject.get("due_date").asText();
        LocalDate dueDate = LocalDate.parse(date, formatter);
        Person claimedFrom = new Person(email);

        UUID expenseUUID = expense.getId();
        DataRepository.getInstance().getExpense(expenseUUID).get().createClaim(claimedFrom, amount, dueDate);
        Set<Claim> claims = DataRepository.getInstance().getExpense(expenseUUID).get().getClaims();
        int claimsIndex = claims.size();

        System.out.println(claimsIndex);
        context.sessionAttribute("claimsIndex", claimsIndex);
        System.out.println(claimedFrom.getEmail());
        HashMap hashMap = new HashMap();

        hashMap.put("id", claimsIndex);
        hashMap.put("claimFromWho", claimedFrom.getEmail());
        hashMap.put("dueDate", dueDate.toString());
        hashMap.put("claimAmount", amount);

        context.sessionAttribute("claims", claims);

        context.sessionAttribute("claimsTotal", getTotalClaimsAmount(claims));
        context.sessionAttribute("unclaimedAmount", getUnclaimedAmount(claims, expense));


        context.json(hashMap);
    }

    private static Double getUnclaimedAmount(Set<Claim> claims, Expense expense) {
        Double amount = expense.getAmount();
        Double totalClaims = getTotalClaimsAmount(claims);
        amount = amount - totalClaims;
        return amount;
    }

    private static Double getTotalClaimsAmount(Set<Claim> claims) {
        Double result = 0.0;
        for(Claim claim: claims) {
            result += claim.getAmount();
        }
        return result;
    }
}