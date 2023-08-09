package ca.yorku.eecs.API;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;

import org.json.JSONObject;

import ca.yorku.eecs.Utils;
import ca.yorku.eecs.ServerCalls.DatabaseExecutor;
import ca.yorku.eecs.models.ActorModel;
import ca.yorku.eecs.models.MovieModel;

public class BaconController extends BaseController{
    
  public BaconController(DatabaseExecutor newDB) {
        super(newDB);
    }

protected void computeBaconNumber(HttpExchange exchange) throws IOException {
      ActorModel actor = new ActorModel();
      MovieModel movie = new MovieModel();
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

    actor.setId(queryParams.get("actorId"));
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfActorIdExists(actor)) {
      sendResponse(exchange, 404, "'actorId' does not exist on database.");
      return;
    }
    JSONObject jsonObject = new JSONObject();
    /* If `actorId` is that of Kevin Bacon, return 0 */
    if (actor.getId().equals(DatabaseExecutor.kevinBaconActorId)) {
      jsonObject.put("baconNumber", 0);
      sendResponse(exchange, 200, jsonObject.toString());
      return;
    }
    Integer baconNumber = databaseExecutor.getBaconNumber(actor.getId());
    /* No path exists to the `actorId` */
    if (baconNumber == 0) {
      sendResponse(exchange, 404, "No path exists.");
    }
    jsonObject.put("baconNumber", baconNumber);
    sendResponse(exchange, 200, jsonObject.toString());
  }

  protected void computeBaconPath(HttpExchange exchange) throws IOException {
    /* Unsupported HTTP method to this API endpoint */
        ActorModel actor = new ActorModel();
      MovieModel movie = new MovieModel();
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
    actor.setId(queryParams.get("actorId"));
    /* Check if 'actorId' exists in database */
    if (!databaseExecutor.checkIfActorIdExists(actor)) {
      sendResponse(exchange, 404, "'actorId' does not exist on database.");
      return;
    }
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    /* Edge case 3 */
    if (actor.getId().equals(DatabaseExecutor.kevinBaconActorId)) {
      jsonArray.put(actor.getId());
      jsonObject.put("baconPath", jsonArray);
      sendResponse(exchange, 200, jsonObject.toString());
      return;
    }
    /* Always returns one path */
    List<String> baconPath = databaseExecutor.getBaconPath(actor.getId());
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


  
}
