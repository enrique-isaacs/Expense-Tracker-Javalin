package weshare.controller;

import java.util.Collection;
import java.util.Map;

import io.javalin.http.Handler;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

public class PaymentRequestsController {
    

    public static final Handler paymentRequestSentView = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsSent = expensesDAO.findPaymentRequestsSent(personLoggedIn);
        Map<String, Object> viewModel = Map.of("paymentRequest", paymentRequestsSent);
        context.render("paymentrequests_sent.html", viewModel);
    };


    public static final Handler paymentRequestReceivedView = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        Map<String, Object> viewModel = Map.of("paymentRequest", paymentRequestsReceived);
        context.render("paymentrequests_received.html", viewModel);
    };
}
