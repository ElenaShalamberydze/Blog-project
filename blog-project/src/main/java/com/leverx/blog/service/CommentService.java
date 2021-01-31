package com.leverx.blog.service;

import com.leverx.blog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Comment findCommentById(int commentId, int articleId);

    void addComment(Comment comment, int articleId);

    void deleteCommentById(int commentId, int articleId);

    Page<Comment> findArticlesComments(int articleId, Pageable pageable);

}
