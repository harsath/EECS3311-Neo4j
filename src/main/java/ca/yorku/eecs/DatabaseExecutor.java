package ca.yorku.eecs;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;

public class DatabaseExecutor {
  private Driver driver;
  private String uriDb;
  /* Database credentials are as mentioned in the project handout */
  private static String username = "neo4j";
  private static String password = "12345678";
  public static String kevinBaconActorId = "nm0000102";

  public DatabaseExecutor() {
    uriDb = "bolt://localhost:7687";
    Config config = Config.builder().withoutEncryption().build();
    driver = GraphDatabase.driver(uriDb, AuthTokens.basic(username, password),
                                  config);
  }

  public void addActor(String id, String name) {
    try (Session session = driver.session()) {
      session.writeTransaction(
          tx
          -> tx.run("MERGE (a:actor {id: $id, name: $name})",
                    parameters("id", id, "name", name)));
    }
  }

  public void addAward(String actorId, String movieId, String award) {
    try (Session session = driver.session()) {
      session.writeTransaction(
          tx
          -> tx.run(
              "MATCH (a:actor {id: $actorId})-[r:ACTED_IN]->(m:movie {id: $movieId}) SET r.award = $award",
              parameters("actorId", actorId, "movieId", movieId, "award",
                         award)));
    }
  }

  public void addMovie(String id, String name) {
    try (Session session = driver.session()) {
      session.writeTransaction(
          tx
          -> tx.run("MERGE (a:movie {id: $id, name: $name})",
                    parameters("id", id, "name", name)));
    }
  }

  public void addRelationship(String movieId, String actorId) {
    try (Session session = driver.session()) {
      session.writeTransaction(
          tx
          -> tx.run(
              "MATCH (a:actor), (m:movie) WHERE a.id = $actorId AND m.id = $movieId CREATE (a)-[r:ACTED_IN]->(m)",
              parameters("actorId", actorId, "movieId", movieId)));
    }
  }

  public boolean checkIfActorIdExists(String actorId) {
    try (Session session = driver.session()) {
      StatementResult result =
          session.run("MATCH (n:actor {id: $actorId}) RETURN n",
                      parameters("actorId", actorId));
      return result.hasNext();
    }
  }

  public boolean checkIfMovieIdExists(String movieId) {
    try (Session session = driver.session()) {
      StatementResult result =
          session.run("MATCH (n:movie {id: $movieId}) RETURN n",
                      parameters("movieId", movieId));
      return result.hasNext();
    }
  }

  public boolean checkIfRelationshipExists(String movieId, String actorId) {
    try (Session session = driver.session()) {
      StatementResult result = session.writeTransaction(
          tx
          -> tx.run(
              "MATCH (a:actor)-[:ACTED_IN]->(m:movie) WHERE a.id = $actorId AND m.id = $movieId RETURN a, m",
              parameters("actorId", actorId, "movieId", movieId)));
      return result.hasNext();
    }
  }

  public List<String> getBaconPath(String startNodeId) {
    List<String> returner = new ArrayList<>();
    try (Session session = driver.session()) {
      String cypherQuery =
          "MATCH p=shortestPath((start:actor {id: $startNodeId})-[*]-(end:actor {id: $endNodeId})) RETURN p";
      StatementResult result =
          session.run(cypherQuery, parameters("startNodeId", startNodeId,
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

  public List<String> getAward(String movieId, String award) {
    List<String> returner = new ArrayList<>();
    try (Session session = driver.session()) {
      String cypherQuery =
          "MATCH (a:actor)-[r:ACTED_IN]->(m:movie {id: $movieId}) WHERE r.award = $award RETURN a.name as actorName";
      StatementResult result = session.run(
          cypherQuery, parameters("award", award, "movieId", movieId));
      while (result.hasNext()) {
        returner.add(result.next().get("actorName").asString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returner;
  }

  public Integer getBaconNumber(String startNodeId) {
    Integer returner = 0;
    try (Session session = driver.session()) {
      String cypherQuery =
          "MATCH p=shortestPath((start:actor {id: $startNodeId})-[*]-(end:actor {id: $endNodeId})) RETURN p";
      StatementResult result =
          session.run(cypherQuery, parameters("startNodeId", startNodeId,
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

  public Pair<String, List<String>> getActor(String actorId) {
    Pair<String, List<String>> returner = new Pair<>("", new ArrayList<>());
    try (Session session = driver.session()) {
      StatementResult actorNameResult =
          session.run("MATCH (a:actor {id: $actorId}) RETURN a.name AS name",
                      parameters("actorId", actorId));
      returner.a = actorNameResult.next().get("name").asString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try (Session session = driver.session()) {
      StatementResult actorMovieIdsResult = session.run(
          "MATCH (a:actor {id: $actorId})-[r]->(m) RETURN m.id AS movieId",
          parameters("actorId", actorId));
      for (Record record : actorMovieIdsResult.list()) {
        String val = record.get("movieId").asString();
        returner.b.add(val);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returner;
  }

  public Pair<String, List<String>> getMovie(String movieId) {
    Pair<String, List<String>> returner = new Pair<>("", new ArrayList<>());
    try (Session session = driver.session()) {
      StatementResult movieNameResult =
          session.run("MATCH (a:movie {id: $movieId}) RETURN a.name AS name",
                      parameters("movieId", movieId));
      returner.a = movieNameResult.next().get("name").asString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try (Session session = driver.session()) {
      StatementResult actorMovieIdsResult = session.run(
          "MATCH (m:movie {id: $movieId})<-[:ACTED_IN]-(a:actor) RETURN a.id as actorId",
          parameters("movieId", movieId));
      for (Record record : actorMovieIdsResult.list()) {
        String val = record.get("actorId").asString();
        returner.b.add(val);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returner;
  }

  public void clearDatabase() {
    try (Session session = driver.session()) {
      session.writeTransaction(tx -> tx.run("MATCH (n) DETACH DELETE n"));
    }
  }

  public void close() { driver.close(); }
}
