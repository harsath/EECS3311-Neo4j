package ca.yorku.eecs;

import static org.neo4j.driver.v1.Values.parameters;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

public class DatabaseExecutor {
  private Driver driver;
  private String uriDb;
  /* Database credentials are as mentioned in the project handout */
  private static String username = "neo4j";
  private static String password = "12345678";

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
      StatementResult result = session.run("MATCH (n:actor {id: $actorId}) RETURN n",
                                           parameters("actorId", actorId));
      return result.hasNext();
    }
  }

  public boolean checkIfMovieIdExists(String movieId) {
    try (Session session = driver.session()) {
      StatementResult result = session.run("MATCH (n:movie {id: $movieId}) RETURN n",
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
public String getMovieName(String movieId) {
    try (Session session = driver.session()) {
        StatementResult result = session.run("MATCH (n:movie {id: $movieId}) RETURN n.name",
                                             parameters("movieId", movieId));
   
            return result.next().get("n.name").asString();

}
  public void close() { driver.close(); }
}
