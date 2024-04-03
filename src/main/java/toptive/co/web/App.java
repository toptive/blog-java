package toptive.co.web;

import static spark.Spark.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import java.io.StringWriter;
import java.util.Properties;

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
            // Add other posts similarly
        });

        // Configure Velocity
        VelocityEngine velocityEngine = new VelocityEngine();
        Properties props = new Properties();
        props.put("file.resource.loader.path", "src/main/resources/templates");
        velocityEngine.init(props);

        // Define the route for the home page
        get("/", (req, res) -> {
            var posts = jdbi.withExtension(PostDao.class, dao -> dao.listPosts());
            return render(velocityEngine, posts, "index.vm");
        });
    }

    private static String render(VelocityEngine velocityEngine, Object model, String templatePath) {
        Template template = velocityEngine.getTemplate(templatePath);
        VelocityContext context = new VelocityContext();
        context.put("posts", model);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
}
