package toptive.co.web;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;

import java.util.List;

public interface PostDao {
    @SqlQuery("SELECT * FROM posts")
    @RegisterBeanMapper(Post.class)
    List<Post> listPosts();

    @SqlUpdate("INSERT INTO posts (title, content) VALUES (:title, :content)")
    void insert(@Bind("title") String title, @Bind("content") String content);
}
