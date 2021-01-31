package com.leverx.blog.repository;

import com.leverx.blog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Integer> {

    Optional<Comment> findByIdAndArticle_Id(int commentId, int articleId);

    Comment save(Comment comment);

    void deleteByIdAndArticle_Id(int commentId, int articleId);

    boolean existsById(int id);

    Page<Comment> findByArticle_Id(int articleId, Pageable pageable);

}
