package toptive.co.web;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import java.io.StringWriter;
import java.util.Properties;
import com.google.gson.Gson;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        // Configure and start Spark framework
        port(4567);

        // Setup JDBI to connect to the H2 database
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE posts (id IDENTITY, title VARCHAR(255), content TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP())");
        });

        // Seed the database with some data
        jdbi.useExtension(PostDao.class, dao -> {
            dao.insert("How to create a blog", "In this tutorial we are going to create a blog");
            dao.insert("How to create a REST API", "In this tutorial we are going to create a REST API");
        });

        get("/", (req, res) -> {
            res.type("application/json");
            return new Gson().toJson(
                new StandardResponse(StatusResponse.SUCCESS, new Gson()
                    .toJsonTree(Map.of("hello","world", "faa", "boo"))
            ));
        });

        get("/posts", (req, res) -> {
            var posts = jdbi.withExtension(PostDao.class, dao -> dao.listPosts());

            res.type("application/json");
            return new Gson().toJson(
                new StandardResponse(StatusResponse.SUCCESS, new Gson()
                    .toJsonTree(posts)));
        });
    }
}
