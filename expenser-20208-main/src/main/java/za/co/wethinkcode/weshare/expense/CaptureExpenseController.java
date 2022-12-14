package za.co.wethinkcode.weshare.expense;

import io.javalin.http.Context;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.nettexpenses.NettExpensesController;

import java.time.LocalDate;
import java.util.Map;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.db.memory.MemoryDb;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.util.Strings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

/**
 * Controller for handling API calls for Expenses
 */
public class CaptureExpenseController {

    public static final String PATH = "/newexpense";
    public static final String SUBMIT = "/home";

    public static void showForm(Context context) {
        context.render("expenseform.html");
    }

    public static void createExpense(Context context){
        DataRepository dataInterface = DataRepository.getInstance();

        Person person = context.sessionAttribute("user");
        double amount = Double.parseDouble(context.formParam("amount"));
        LocalDate date = LocalDate.parse(context.formParam("date"));
        String description   = context.formParam("description");

        Expense sendExpense = new Expense(amount, date, description,person);
        dataInterface.addExpense(sendExpense);

        context.redirect(NettExpensesController.PATH);
    }
}