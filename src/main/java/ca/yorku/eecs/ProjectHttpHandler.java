package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class ProjectHttpHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
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
    String response = "addActor";
    sendResponse(exchange, 200, response);
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

  private void sendResponse(HttpExchange exchange, int statusCode, String response)
      throws IOException {
    exchange.sendResponseHeaders(statusCode, response.length());
    OutputStream outputStream = exchange.getResponseBody();
    outputStream.write(response.getBytes());
    outputStream.close();
  }
}
