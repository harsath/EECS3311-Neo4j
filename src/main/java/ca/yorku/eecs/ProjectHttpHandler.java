package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONException;
import org.json.JSONObject;

public class ProjectHttpHandler implements HttpHandler {
  private DatabaseExecutor databaseExecutor = null;
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if (databaseExecutor == null) {
      databaseExecutor = new DatabaseExecutor();
    }
    String requestURI = exchange.getRequestURI().toString();
    switch (requestURI) {
    case "/api/v1/addActor":
      addActor(exchange);
      break;
    case "/api/v1/addMovie":
      addMovie(exchange);
      break;
    case "/api/v1/addRelationship":
      addRelationship(exchange);
      break;
    case "/api/v1/getActor":
      getActor(exchange);
      break;
    case "getMovie":
      getMovie(exchange);
      break;
    case "/api/v1/hasRelationship":
      hasRelationship(exchange);
      break;
    case "/api/v1/computeBaconNumber":
      computeBaconNumber(exchange);
      break;
    case "/api/v1/computeBaconPath":
      computeBaconPath(exchange);
      break;
    default:
      invalidRequest(exchange);
    }
  }

  private void addActor(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "PUT")) {
      sendResponse(exchange, 405, "Endpoint only accepts PUT requests.");
      return;
    }
    String requestBody = Utils.getBody(exchange);
    JSONObject jsonHttpBody;
    /* Invalid JSON format */
    try {
      jsonHttpBody = Utils.convertToJSONObject(requestBody);
    } catch (JSONException e) {
      sendResponse(exchange, 400, "Invalid JSON string.");
      return;
    }
    /* Check if JSON property 'name' exists */
    if (!jsonHttpBody.has("name")) {
      sendResponse(exchange, 400, "Must include 'name'.");
      return;
    }
    /* Check if JSON property 'actorId' exists */
    if (!jsonHttpBody.has("actorId")) {
      sendResponse(exchange, 400, "Must include 'actorId'.");
      return;
    }
    /* Edge case */
    if (databaseExecutor.checkIfActorIdExists(
            jsonHttpBody.getString("actorId"))) {
      sendResponse(exchange, 400, "The 'actorId' already exists.");
      return;
    }
    /* Add to database, or send HTTP 500 in case of database error */
    try {
      databaseExecutor.addActor(jsonHttpBody.getString("actorId"),
                                jsonHttpBody.getString("name"));
    } catch (Exception e) {
      e.printStackTrace();
      sendResponse(exchange, 500, "Database error.");
      return;
    }
    sendResponse(exchange, 200, "Added successfully.");
  }

  private void addMovie(HttpExchange exchange) throws IOException {
    String response = "addMovie";
    sendResponse(exchange, 200, response);
  }

  private void addRelationship(HttpExchange exchange) throws IOException {
    String response = "addRelationship";
    sendResponse(exchange, 404, response);
  }

  private void getActor(HttpExchange exchange) throws IOException {
    String response = "getActor";
    sendResponse(exchange, 404, response);
  }

  private void getMovie(HttpExchange exchange) throws IOException {
    String response = "getMovie";
    sendResponse(exchange, 404, response);
  }

  private void hasRelationship(HttpExchange exchange) throws IOException {
    String response = "hasRelationship";
    sendResponse(exchange, 404, response);
  }

  private void computeBaconNumber(HttpExchange exchange) throws IOException {
    String response = "computeBaconNumber";
    sendResponse(exchange, 404, response);
  }

  private void computeBaconPath(HttpExchange exchange) throws IOException {
    String response = "computeBaconPath";
    sendResponse(exchange, 404, response);
  }

  private void invalidRequest(HttpExchange exchange) throws IOException {
    String response = "invalidRequest";
    sendResponse(exchange, 404, response);
  }

  private void sendResponse(HttpExchange exchange, int statusCode,
                            String response) throws IOException {
    exchange.sendResponseHeaders(statusCode, response.length());
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write(response.getBytes());
    outputStream.close();
  }

  private boolean checkRequestMethod(HttpExchange exchange,
                                     String requestMethod) {
    return !(exchange.getRequestMethod().equals(requestMethod));
  }
}
