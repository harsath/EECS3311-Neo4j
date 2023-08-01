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

  public boolean checkIfActorIdExists(String actorId) {
    try (Session session = driver.session()) {
      StatementResult result = session.run("MATCH (n {id: $actorId}) RETURN n",
                                           parameters("actorId", actorId));
      return result.hasNext();
    }
  }

  public void close() { driver.close(); }
}
