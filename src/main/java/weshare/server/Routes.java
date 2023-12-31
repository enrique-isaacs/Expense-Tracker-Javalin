package weshare.server;

import weshare.controller.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Routes {
    public static final String LOGIN_PAGE = "/";
    public static final String LOGIN_ACTION = "/login.action";
    public static final String LOGOUT = "/logout";
    public static final String EXPENSES = "/expenses";
    public static final String NEWEXPENSES = "/newexpense";
    public static final String PAYMENTREQUESTSENT = "/paymentrequests_sent";
    public static final String PAYMENTREQUEST = "/paymentrequest";
    public static final String PAYMENTREQUESTACTION = "/paymentrequest.action";
    public static final String PAYMENTREQUESTRECEIVED = "/paymentrequests_received";
    public static final String CREATEEXPENSE = "/expense.action";
    public static final String PAYMENTACTION = "/payment.action";

    public static void configure(WeShareServer server) {
        server.routes(() -> {
            post(LOGIN_ACTION,  PersonController.login);
            get(LOGOUT,         PersonController.logout);
            get(EXPENSES,           ExpensesController.view);
            get(PAYMENTREQUESTSENT, PaymentRequestsController.paymentRequestSentView);
            get(PAYMENTREQUEST, PaymentRequestsController.paymentRequestView);
            get(PAYMENTREQUESTRECEIVED, PaymentRequestsController.paymentRequestReceivedView);
            get(NEWEXPENSES,           ExpensesController.newexpense);
            post(CREATEEXPENSE, ExpensesController.expenseAction);
            post(PAYMENTREQUESTACTION, PaymentRequestsController.paymentRequest);
            post(PAYMENTACTION, PaymentRequestsController.payment);
        });
    }
}
