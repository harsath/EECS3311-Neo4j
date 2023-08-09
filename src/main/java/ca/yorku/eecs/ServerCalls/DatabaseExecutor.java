package ca.yorku.eecs.ServerCalls;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;

import ca.yorku.eecs.models.ActorModel;
import ca.yorku.eecs.models.MovieModel;

public class DatabaseExecutor {
  private Driver driver;
  private String uriDb;

  /* Database credentials are as mentioned in the project handout */
  private static String username = "neo4j";
  private static String password = "12345678";

  /*
   * TODO:
   * Create global Class to keep all public objects as required
   */
  public static String kevinBaconActorId = "nm0000102";

  public DatabaseExecutor() {
    uriDb = "bolt://localhost:7687";
    Config config = Config.builder().withoutEncryption().build();
    driver = GraphDatabase.driver(uriDb, AuthTokens.basic(username, password), config);
  }

  public void addActor(ActorModel actor) {
    try (Session session = driver.session()) {
      session.writeTransaction(
          tx -> tx.run("MERGE (a:actor {id: $id, name: $name})",
              parameters("id", actor.getId(), "name", actor.getName())));
    }
  }

  public void addMovie(MovieModel movie) {
    try (Session session = driver.session()) {
      session.writeTransaction(
          tx -> tx.run("MERGE (a:movie {id: $id, name: $name})",
              parameters("id", movie.getId(), "name", movie.getName())));
    }
  }

  public void addRelationship(MovieModel movie, ActorModel actor) {
    try (Session session = driver.session()) {
      session.writeTransaction(
          tx -> tx.run(
              "MATCH (a:actor), (m:movie) WHERE a.id = $actorId AND m.id = $movieId CREATE (a)-[r:ACTED_IN]->(m)",
              parameters("actorId", actor.getId(), "movieId", movie.getId())));
    }
  }

  public boolean checkIfActorIdExists(ActorModel actor) {
    try (Session session = driver.session()) {
      StatementResult result = session.run("MATCH (n:actor {id: $actorId}) RETURN n",
          parameters("actorId", actor.getId()));
      return result.hasNext();
    }
  }

  public boolean checkIfMovieIdExists(MovieModel movie) {
    try (Session session = driver.session()) {
      StatementResult result = session.run("MATCH (n:movie {id: $movieId}) RETURN n",
          parameters("movieId", movie.getId()));
      return result.hasNext();
    }
  }

  public boolean checkIfRelationshipExists(MovieModel movie, ActorModel actor) {
    try (Session session = driver.session()) {
      StatementResult result = session.writeTransaction(
          tx -> tx.run(
              "MATCH (a:actor)-[:ACTED_IN]->(m:movie) WHERE a.id = $actorId AND m.id = $movieId RETURN a, m",
              parameters("actorId", actor.getId(), "movieId", movie.getId())));
      return result.hasNext();
    }
  }

  public List<String> getBaconPath(String startNodeId) {
    List<String> returner = new ArrayList<>();
    try (Session session = driver.session()) {
      String cypherQuery = "MATCH p=shortestPath((start:actor {id: $startNodeId})-[*]-(end:actor {id: $endNodeId})) RETURN p";
      StatementResult result = session.run(cypherQuery, parameters("startNodeId", startNodeId,
          "endNodeId", kevinBaconActorId));
      if (result.hasNext()) {
        Record record = result.next();
        Path path = record.get("p").asPath();
        Iterable<Node> nodes = path.nodes();
        for (Node node : nodes) {
          returner.add(node.get("id").asString());
        }
      }
    }
    return returner;
  }

  public Integer getBaconNumber(String startNodeId) {
    Integer returner = 0;
    try (Session session = driver.session()) {
      String cypherQuery = "MATCH p=shortestPath((start:actor {id: $startNodeId})-[*]-(end:actor {id: $endNodeId})) RETURN p";
      StatementResult result = session.run(cypherQuery, parameters("startNodeId", startNodeId,
          "endNodeId", kevinBaconActorId));
      if (result.hasNext()) {
        Record record = result.next();
        Path path = record.get("p").asPath();
        Iterable<Node> nodes = path.nodes();
        for (Node node : nodes) {
          Iterator<String> labelIterator = node.labels().iterator();
          while (labelIterator.hasNext()) {
            String label = labelIterator.next();
            /* Check if current node is `movie` */
            if (label.equals("movie")) {
              returner++;
              break;
            }
          }
        }
      }
    }
    return returner;
  }

  public JSONObject getActor(ActorModel actor) {
    JSONObject jsonResult = new JSONObject();

    try (Session session = driver.session()) {

      String movieCypherQuery = "MATCH (a:actor{id: $actorId})-[r]->(m) RETURN m.id";
      String nameCypherQuery = "MATCH (a:actor{id: $actorId}) RETURN a.name";
      try (Transaction tx = session.beginTransaction()) {
        StatementResult movies = tx.run(movieCypherQuery, parameters("actorId", actor.getId()));
        JSONArray listOfMovies = new JSONArray();
        while (movies.hasNext()) {
          Record record = movies.next();
          Value movieId = record.get("m.id");

          if (!movieId.isNull()) {
            listOfMovies.put(movieId.asString());
          }
        }
        StatementResult actorNameSearch = tx.run(nameCypherQuery, parameters("actorId", actor.getId()));

        jsonResult.put("actorId", actor.getId());
        jsonResult.put("name", actorNameSearch.next().get("a.name").asString());
        jsonResult.put("movies", listOfMovies);

        return jsonResult;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public JSONObject getMovie(MovieModel movie) {
    JSONObject jsonResult = new JSONObject();

    try (Session session = driver.session()) {
      String actorCypherQuery = "MATCH (a)-[r]->(m:movie{id: $movieId}) RETURN a.id";
      String nameCypherQuery = "MATCH (m:movie{id: $movieId}) RETURN m.name";
      try (Transaction tx = session.beginTransaction()) {
        StatementResult actors = tx.run(actorCypherQuery, parameters("movieId", movie.getId()));
        JSONArray listOfActors = new JSONArray();
        while (actors.hasNext()) {
          Record record = actors.next();
          Value actorId = record.get("a.id");

          if (!actorId.isNull()) {
            listOfActors.put(actorId.asString());
          }
        }
          StatementResult movieNameSearch = tx.run(nameCypherQuery, parameters("movieId", movie.getId()));

          jsonResult.put("movieId", movie.getId());
          jsonResult.put("name", movieNameSearch.next().get("m.name").asString());
          jsonResult.put("actors", listOfActors);

          return jsonResult;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public void clearDatabase() {
    try (Session session = driver.session()) {
      session.writeTransaction(tx -> tx.run("MATCH (n) DETACH DELETE n"));
    }
  }

  public void close() {
    driver.close();
  }
}
