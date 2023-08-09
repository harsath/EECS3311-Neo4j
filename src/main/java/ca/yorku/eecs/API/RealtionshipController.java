package ca.yorku.eecs.API;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import ca.yorku.eecs.Utils;
import ca.yorku.eecs.ServerCalls.DatabaseExecutor;
import ca.yorku.eecs.models.ActorModel;
import ca.yorku.eecs.models.MovieModel;

public class RealtionshipController extends BaseController {

    public RealtionshipController(DatabaseExecutor newDB) {
        super(newDB);
        // TODO Auto-generated constructor stub
    }

    protected void addRelationship(HttpExchange exchange) throws IOException {
        MovieModel movie = new MovieModel();
        ActorModel actor = new ActorModel();
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
            sendResponse(exchange, 400, "Invalid JS ON string.");
            return;
        }
        /* Check if JSON property 'actorId' exists */
        if (jsonHttpBody.has("actorId")) {
            actor.setId(jsonHttpBody.getString("actorId"));
        } else {
            sendResponse(exchange, 400, "Must include 'actorId'.");
            return;
        }
        /* Check if JSON property 'movieId' exists */
        if (jsonHttpBody.has("movieId")) {
            movie.setId(jsonHttpBody.getString("movieId"));
        } else {
            sendResponse(exchange, 400, "Must include 'movieId'.");
            return;
        }
       /* Check if 'actorId' exists in database */
        if (!databaseExecutor.checkIfActorIdExists(actor)) {
            sendResponse(exchange, 404, "'actorId' does not exist on database.");
            return;
        }
        /* Check if 'movieId' exists in database */
        if (!databaseExecutor.checkIfMovieIdExists(movie)) {
            sendResponse(exchange, 404, "'movieId' does not exist on database.");
            return;
        }
        /* Edge case */
        if (databaseExecutor.checkIfRelationshipExists(movie, actor)) {
            sendResponse(exchange, 400, "Relationship already exists.");
            return;
        }
        /* Add to database, or send HTTP 500 in case of database error */
        try {
            databaseExecutor.addRelationship(movie, actor);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Database error.");
            return;
        }
        sendResponse(exchange, 200, "Added successfully.");
    }

    protected void hasRelationship(HttpExchange exchange) throws IOException {
        MovieModel movie = new MovieModel();
        ActorModel actor = new ActorModel();
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
        if (queryParams.containsKey("actorId")) {
            actor.setId(queryParams.get("actorId"));
        } else {
            sendResponse(exchange, 400, "Must include 'actorId'.");
            return;
        }
        /* Check if query property 'movieId' exists */
        if (queryParams.containsKey("movieId")) {
            movie.setId(queryParams.get("movieId"));
        } else {
            sendResponse(exchange, 400, "Must include 'movieId'.");
            return;
        }
        /* Check if 'actorId' exists in database */
        if (!databaseExecutor.checkIfActorIdExists(actor)) {
            sendResponse(exchange, 404, "'actorId' does not exist on database.");
            return;
        }
        /* Check if 'movieId' exists in database */
        if (!databaseExecutor.checkIfMovieIdExists(movie)) {
            sendResponse(exchange, 404, "'movieId' does not exist on database.");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("actorId", actor.getId());
        jsonObject.put("movieId", movie.getId());
        /* Send response if relationship exists, or not */
        if (databaseExecutor.checkIfRelationshipExists(movie, actor)) {
            jsonObject.put("hasRelationship", true);
            sendResponse(exchange, 200, jsonObject.toString());
            return;
        }
        jsonObject.put("hasRelationship", false);
        sendResponse(exchange, 200, jsonObject.toString());
    }
}
