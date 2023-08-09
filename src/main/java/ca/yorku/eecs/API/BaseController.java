package ca.yorku.eecs.API;

import com.sun.net.httpserver.HttpExchange;

import ca.yorku.eecs.ServerCalls.DatabaseExecutor;

import java.io.IOException;
import java.io.OutputStream;


public class BaseController {

    protected DatabaseExecutor databaseExecutor = null;

    public BaseController(DatabaseExecutor newDB) {
        databaseExecutor = newDB;
    }

    protected void clearDatabase(HttpExchange exchange) throws IOException {
        /* Unsupported HTTP method to this API endpoint */
        if (checkRequestMethod(exchange, "GET")) {
            sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
            return;
        }
        databaseExecutor.clearDatabase();
        String response = "Cleared database.";
        sendResponse(exchange, 200, response);
    }

    protected void invalidRequest(HttpExchange exchange) throws IOException {
        String response = "invalid Request";
        sendResponse(exchange, 400, response);
    }

    protected void sendResponse(HttpExchange exchange, int statusCode,
            String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    protected boolean checkRequestMethod(HttpExchange exchange,
            String requestMethod) {
        return !(exchange.getRequestMethod().equals(requestMethod));
    }
}
