package ca.yorku.eecs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProjectHttpHandler implements HttpHandler {
  private DatabaseExecutor databaseExecutor = null;
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    if (databaseExecutor == null) {
      databaseExecutor = new DatabaseExecutor();
    }
    String requestPath = exchange.getRequestURI().getPath();
    switch (requestPath) {
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
    case "/api/v1/getMovie":
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
    case "/api/v1/addAward":
      addAward(exchange);
      break;
    case "/api/v1/getAward":
      getAward(exchange);
      break;
    case "/api/v1/clearDatabase":
      clearDatabase(exchange);
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
    /* Check if JSON property 'movieId' exists */
    if (!jsonHttpBody.has("movieId")) {
      sendResponse(exchange, 400, "Must include 'movieId'.");
      return;
    }
    /* Edge case */
    if (databaseExecutor.checkIfMovieIdExists(
            jsonHttpBody.getString("movieId"))) {
      sendResponse(exchange, 400, "The 'movieId' already exists.");
      return;
    }
    /* Add to database, or send HTTP 500 in case of database error */
    try {
      databaseExecutor.addMovie(jsonHttpBody.getString("movieId"),
                                jsonHttpBody.getString("name"));
    } catch (Exception e) {
      e.printStackTrace();
      sendResponse(exchange, 500, "Database error.");
      return;
    }
    sendResponse(exchange, 200, "Added successfully.");
  }

  private void addRelationship(HttpExchange exchange) throws IOException {
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
    /* Check if JSON property 'actorId' exists */
    if (!jsonHttpBody.has("actorId")) {
      sendResponse(exchange, 400, "Must include 'actorId'.");
      return;
    }
    /* Check if JSON property 'movieId' exists */
    if (!jsonHttpBody.has("movieId")) {
      sendResponse(exchange, 400, "Must include 'movieId'.");
      return;
    }
    /* Edge case */
    if (databaseExecutor.checkIfRelationshipExists(
            jsonHttpBody.getString("movieId"),
            jsonHttpBody.getString("actorId"))) {
      sendResponse(exchange, 400, "Relationship already exists.");
      return;
    }
    /* Add to database, or send HTTP 500 in case of database error */
    try {
      databaseExecutor.addRelationship(jsonHttpBody.getString("movieId"),
                                       jsonHttpBody.getString("actorId"));
    } catch (Exception e) {
      e.printStackTrace();
      sendResponse(exchange, 500, "Database error.");
      return;
    }
    sendResponse(exchange, 200, "Added successfully.");
  }

  private void getActor(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "GET")) {
      sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
      return;
    }
    String rawQuery = exchange.getRequestURI().getRawQuery();
    /* Make sure useragent passes query-string */
    if (rawQuery == null || rawQuery.isEmpty()) {
      sendResponse(exchange, 405, "Must pass query to this endpoint.");
      return;
    }
    /* Parse query string into a hashmap */
    Map<String, String> queryParams = Utils.splitQuery(rawQuery);
    /* Check if query property 'actorId' exists */
    if (!queryParams.containsKey("actorId")) {
      sendResponse(exchange, 400, "Must include 'actorId'.");
      return;
    }
    String actorId = queryParams.get("actorId");
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfActorIdExists(actorId)) {
      sendResponse(exchange, 404, "'actorId' does not exist on database.");
      return;
    }
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    Pair<String, List<String>> getActorQuery =
        databaseExecutor.getActor(actorId);
    for (String movieId : getActorQuery.b) {
      jsonArray.put(movieId);
    }
    jsonObject.put("movies", jsonArray);
    jsonObject.put("name", getActorQuery.a);
    jsonObject.put("actorId", actorId);
    sendResponse(exchange, 200, jsonObject.toString());
  }

  private void getMovie(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "GET")) {
      sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
      return;
    }
    String rawQuery = exchange.getRequestURI().getRawQuery();
    /* Make sure useragent passes query-string */
    if (rawQuery == null || rawQuery.isEmpty()) {
      sendResponse(exchange, 405, "Must pass query to this endpoint.");
      return;
    }
    /* Parse query string into a hashmap */
    Map<String, String> queryParams = Utils.splitQuery(rawQuery);
    /* Check if query property 'movieId' exists */
    if (!queryParams.containsKey("movieId")) {
      sendResponse(exchange, 400, "Must include 'movieId'.");
      return;
    }
    String movieId = queryParams.get("movieId");
    /* Check if 'movieId' exists in database */
    if (!databaseExecutor.checkIfMovieIdExists(movieId)) {
      sendResponse(exchange, 404, "'movieId' does not exist on database.");
      return;
    }
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    Pair<String, List<String>> getMovieQuery =
        databaseExecutor.getMovie(movieId);
    for (String actorId : getMovieQuery.b) {
      jsonArray.put(actorId);
    }
    jsonObject.put("actors", jsonArray);
    jsonObject.put("name", getMovieQuery.a);
    jsonObject.put("movieId", movieId);
    sendResponse(exchange, 200, jsonObject.toString());
  }

  private void hasRelationship(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "GET")) {
      sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
      return;
    }
    String rawQuery = exchange.getRequestURI().getRawQuery();
    /* Make sure useragent passes query-string */
    if (rawQuery == null || rawQuery.isEmpty()) {
      sendResponse(exchange, 405, "Must pass query to this endpoint.");
      return;
    }
    /* Parse query string into a hashmap */
    Map<String, String> queryParams = Utils.splitQuery(rawQuery);
    /* Check if query property 'actorId' exists */
    if (!queryParams.containsKey("actorId")) {
      sendResponse(exchange, 400, "Must include 'actorId'.");
      return;
    }
    /* Check if query property 'movieId' exists */
    if (!queryParams.containsKey("movieId")) {
      sendResponse(exchange, 400, "Must include 'movieId'.");
      return;
    }
    String actorId = queryParams.get("actorId");
    String movieId = queryParams.get("movieId");
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfActorIdExists(actorId)) {
      sendResponse(exchange, 404, "'actorId' does not exist on database.");
      return;
    }
    /* Check if 'movieId' exists in database */
    if (!databaseExecutor.checkIfMovieIdExists(movieId)) {
      sendResponse(exchange, 404, "'movieId' does not exist on database.");
      return;
    }
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("actorId", actorId);
    jsonObject.put("movieId", movieId);
    /* Send response if relationship exists, or not */
    if (databaseExecutor.checkIfRelationshipExists(
            queryParams.get("movieId"), queryParams.get("actorId"))) {
      jsonObject.put("hasRelationship", true);
      sendResponse(exchange, 200, jsonObject.toString());
      return;
    }
    jsonObject.put("hasRelationship", false);
    sendResponse(exchange, 200, jsonObject.toString());
  }

  private void computeBaconNumber(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "GET")) {
      sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
      return;
    }
    String rawQuery = exchange.getRequestURI().getRawQuery();
    /* Make sure useragent passes query-string */
    if (rawQuery == null || rawQuery.isEmpty()) {
      sendResponse(exchange, 405, "Must pass query to this endpoint.");
      return;
    }
    /* Parse query string into a hashmap */
    Map<String, String> queryParams = Utils.splitQuery(rawQuery);
    /* Check if query property 'actorId' exists */
    if (!queryParams.containsKey("actorId")) {
      sendResponse(exchange, 400, "Must include 'actorId'.");
      return;
    }
    String actorId = queryParams.get("actorId");
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfActorIdExists(actorId)) {
      sendResponse(exchange, 404, "'actorId' does not exist on database.");
      return;
    }
    JSONObject jsonObject = new JSONObject();
    /* If `actorId` is that of Kevin Bacon, return 0 */
    if (actorId.equals(DatabaseExecutor.kevinBaconActorId)) {
      jsonObject.put("baconNumber", 0);
      sendResponse(exchange, 200, jsonObject.toString());
      return;
    }
    Integer baconNumber = databaseExecutor.getBaconNumber(actorId);
    /* No path exists to the `actorId` */
    if (baconNumber == 0) {
      sendResponse(exchange, 404, "No path exists.");
    }
    jsonObject.put("baconNumber", baconNumber);
    sendResponse(exchange, 200, jsonObject.toString());
  }

  private void computeBaconPath(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "GET")) {
      sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
      return;
    }
    String rawQuery = exchange.getRequestURI().getRawQuery();
    /* Make sure useragent passes query-string */
    if (rawQuery == null || rawQuery.isEmpty()) {
      sendResponse(exchange, 405, "Must pass query to this endpoint.");
      return;
    }
    /* Parse query string into a hashmap */
    Map<String, String> queryParams = Utils.splitQuery(rawQuery);
    /* Check if query property 'actorId' exists */
    if (!queryParams.containsKey("actorId")) {
      sendResponse(exchange, 400, "Must include 'actorId'.");
      return;
    }
    String actorId = queryParams.get("actorId");
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfActorIdExists(actorId)) {
      sendResponse(exchange, 404, "'actorId' does not exist on database.");
      return;
    }
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    /* Edge case 3 */
    if (actorId.equals(DatabaseExecutor.kevinBaconActorId)) {
      jsonArray.put(actorId);
      jsonObject.put("baconPath", jsonArray);
      sendResponse(exchange, 200, jsonObject.toString());
      return;
    }
    /* Always returns one path */
    List<String> baconPath = databaseExecutor.getBaconPath(actorId);
    /* Edge case 1 */
    if (baconPath.isEmpty()) {
      sendResponse(exchange, 404, "No path exists to the actor.");
      return;
    }
    /* Send response path on response */
    for (String id : baconPath) {
      jsonArray.put(id);
    }
    jsonObject.put("baconPath", jsonArray);
    sendResponse(exchange, 200, jsonObject.toString());
  }

  private void addAward(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "GET")) {
      sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
      return;
    }
    String rawQuery = exchange.getRequestURI().getRawQuery();
    /* Make sure useragent passes query-string */
    if (rawQuery == null || rawQuery.isEmpty()) {
      sendResponse(exchange, 405, "Must pass query to this endpoint.");
      return;
    }
    /* Parse query string into a hashmap */
    Map<String, String> queryParams = Utils.splitQuery(rawQuery);
    /* Check if query property 'actorId' exists */
    if (!queryParams.containsKey("actorId")) {
      sendResponse(exchange, 400, "Must include 'actorId'.");
      return;
    }
    String actorId = queryParams.get("actorId");
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfActorIdExists(actorId)) {
      sendResponse(exchange, 404, "'actorId' does not exist on database.");
      return;
    }
    /* Check if query property 'movieId' exists */
    if (!queryParams.containsKey("movieId")) {
      sendResponse(exchange, 400, "Must include 'movieId'.");
      return;
    }
    String movieId = queryParams.get("movieId");
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfMovieIdExists(movieId)) {
      sendResponse(exchange, 404, "'movieId' does not exist on database.");
      return;
    }
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfRelationshipExists(movieId, actorId)) {
      sendResponse(exchange, 400, "No relationship exists between given 'actorId' and 'movieId'");
      return;
    }
    /* Check if query property 'award' exists */
    if (!queryParams.containsKey("award")) {
      sendResponse(exchange, 400, "Must include 'award'.");
      return;
    }
    String award = queryParams.get("award");
    
    /* Add to database, or send HTTP 500 in case of database error */
    try {
      databaseExecutor.addAward(actorId, movieId, award);
    } catch (Exception e) {
      e.printStackTrace();
      sendResponse(exchange, 500, "Database error.");
      return;
    }
    sendResponse(exchange, 200, "Added successfully.");
  }

  private void getAward(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "GET")) {
      sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
      return;
    }
    String rawQuery = exchange.getRequestURI().getRawQuery();
    /* Make sure useragent passes query-string */
    if (rawQuery == null || rawQuery.isEmpty()) {
      sendResponse(exchange, 405, "Must pass query to this endpoint.");
      return;
    }
    /* Parse query string into a hashmap */
    Map<String, String> queryParams = Utils.splitQuery(rawQuery);
    /* Check if query property 'movieId' exists */
    if (!queryParams.containsKey("movieId")) {
      sendResponse(exchange, 400, "Must include 'movieId'.");
      return;
    }
    String movieId = queryParams.get("movieId");
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfMovieIdExists(movieId)) {
      sendResponse(exchange, 404, "'movieId' does not exist on database.");
      return;
    }
    /* Check if query property 'award' exists */
    if (!queryParams.containsKey("award")) {
      sendResponse(exchange, 400, "Must include 'award'.");
      return;
    }
    String award = queryParams.get("award");
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    /* Get list of actor names with the given `award` in a particular movie */
    List<String> actors = databaseExecutor.getAward(movieId, award);
    /* Send response path on response */
    for (String id : actors) {
      jsonArray.put(id);
    }
    jsonObject.put("actors", jsonArray);
    jsonObject.put("movieId", movieId);
    jsonObject.put("award", award);
    sendResponse(exchange, 200, jsonObject.toString());
  }

  private void clearDatabase(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
    if (checkRequestMethod(exchange, "GET")) {
      sendResponse(exchange, 405, "Endpoint only accepts GET requests.");
      return;
    }
    databaseExecutor.clearDatabase();
    String response = "Cleared database.";
    sendResponse(exchange, 200, response);
  }

  private void invalidRequest(HttpExchange exchange) throws IOException {
    String response = "invalidRequest";
    sendResponse(exchange, 400, response);
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
