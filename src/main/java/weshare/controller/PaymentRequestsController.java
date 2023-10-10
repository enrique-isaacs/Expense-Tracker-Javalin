package weshare.controller;

import java.util.Collection;
import java.util.Map;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

public class PaymentRequestsController {


    public static final Handler requestsReceivedView = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> paymentRequestsReceived = expensesDAO.findExpensesForPerson(personLoggedIn);
        Map<String, Object> viewModel = Map.of("expenses", expenses);
        context.render("expenses.html", viewModel);
    };
    
}
