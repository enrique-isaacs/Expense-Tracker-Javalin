package weshare.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.money.MonetaryAmount;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.MoneyHelper;
import weshare.model.Payment;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.persistence.PersonDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

public class PaymentRequestsController {
    

    public static final Handler paymentRequestSentView = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsSent = expensesDAO.findPaymentRequestsSent(personLoggedIn);

        MonetaryAmount total = MoneyHelper.amountOf(0);
        for(PaymentRequest paymentRequest: paymentRequestsSent){
            total = total.add(paymentRequest.getAmountToPay());
        }

        Map<String, Object> viewModel = Map.of("paymentRequest", paymentRequestsSent, "totalUnpaid", total);
        context.render("paymentrequests_sent.html", viewModel);
    };


    public static final Handler paymentRequestReceivedView = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        MonetaryAmount total = MoneyHelper.amountOf(0);
        for(PaymentRequest paymentRequest : paymentRequestsReceived){
            total = total.add(paymentRequest.getAmountToPay());
        }
        Map<String, Object> viewModel = Map.of("paymentRequest", paymentRequestsReceived, "totalUnpaid", total);
        context.render("paymentrequests_received.html", viewModel);
    };

    public static final Handler paymentRequestView = context -> {

        Map<String, Object> viewModel;
        Collection<PaymentRequest> paymentRequestList = new ArrayList<>();

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        String expenseId = context.queryParam("expenseId");

        Expense expense = expensesDAO.get(UUID.fromString(expenseId)).orElseThrow();
        
        MonetaryAmount total = MoneyHelper.amountOf(0);

        for(PaymentRequest paymentRequest : expensesDAO.findPaymentRequestsSent(personLoggedIn)){
            if(paymentRequest.getExpense().equals(expense)){
                total = total.add(paymentRequest.getAmountToPay());
                paymentRequestList.add(paymentRequest);
            }
            
        }
        
        viewModel = Map.of("expense", expense, "paymentRequestList", paymentRequestList, "grandTotal", total);

        context.render("paymentrequest.html", viewModel);
    };


    /**
     * I handle creating a Payment Request
     * Route calls me when user sends form action
     */
    public static final Handler paymentRequest = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        PersonDAO personDAO = ServiceRegistry.lookup(PersonDAO.class);
        Map<String, List<String>> queryMap = context.formParamMap();
        System.out.println(queryMap);

        String expenseId = queryMap.get("expense_id").get(0);
        Expense expense = expensesDAO.get(UUID.fromString(expenseId)).orElseThrow();

        Person person = personDAO.savePerson(new Person(queryMap.get("email").get(0)));

        MonetaryAmount amount = MoneyHelper.amountOf(Integer.parseInt(queryMap.get("amount").get(0)));

        String dateStr = queryMap.get("due_date").get(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);


        expense.requestPayment(person, amount, date);

        context.redirect(Routes.PAYMENTREQUEST+"?expenseId="+expenseId);
    };

    public static final Handler payment = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        String paymentId = context.formParam("paymentId");

        Collection<PaymentRequest> paymentRequestReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        for(PaymentRequest paymentRequest : paymentRequestReceived){
            if(paymentRequest.getId().equals(UUID.fromString(paymentId))){
                Payment payment = paymentRequest.pay(personLoggedIn, LocalDate.now());
                Expense expense = payment.getExpenseForPersonPaying();
                expensesDAO.save(expense);
            }
        }

        context.redirect(Routes.PAYMENTREQUESTRECEIVED);
    };
}
