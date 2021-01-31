package com.leverx.blog.repository;

import com.leverx.blog.model.Article;
import com.leverx.blog.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends CrudRepository<Article, Integer> {

    Page<Article> findByStatus(Status status, Pageable pageable);

    List<Article> findByUser_Email(String email);

    Article save(Article article);

    void deleteByIdAndUser_Email(int articleId, String email);

    boolean existsById(int id);

    @Modifying
    @Query(value = "UPDATE articles SET status=:status WHERE id=:id", nativeQuery = true)
    void updateStatus(@Param("status") String status, @Param("id") int articleId);

    @Query(value = "SELECT status FROM articles WHERE id=:id", nativeQuery = true)
    String status(@Param("id") int id);

}
