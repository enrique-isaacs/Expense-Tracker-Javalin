package weshare.controller;

import io.javalin.http.Handler;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.jetbrains.annotations.NotNull;
import weshare.model.Expense;
import weshare.model.MoneyHelper;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static weshare.model.MoneyHelper.ZERO_RANDS;

public class ExpensesController {

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        MonetaryAmount totalExpense = totalExpense(expenses);
        Map<String, Object> viewModel = Map.of("expenses", expenses, "totalExpense", totalExpense);
        context.render("expenses.html", viewModel);
    };

    private static MonetaryAmount totalExpense(Collection<Expense> expenses) {
        MonetaryAmount total = MoneyHelper.amountOf(0);
        for (Expense expense : expenses) {
            total = total.add(expense.getAmount());
        }
        return total;
    }

    public static final Handler newexpense = ctx -> {
        
        ctx.render("newexpense.html");
        
    };

    public static final Handler expenseAction = ctx -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(ctx);

        // Extracting form data
        String description = ctx.formParamMap().get("description").get(0);
        MonetaryAmount amount = MoneyHelper.amountOf(Integer.parseInt(ctx.formParamMap().get("amount").get(0)));
        String dateStr = ctx.formParamMap().get("date").get(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);

        // Create expense
        Expense expense = new Expense(personLoggedIn, description, amount, date);
        expensesDAO.save(expense);
        

        ctx.redirect(Routes.EXPENSES);
    };


}
