package com.leverx.blog.service.Impl;

import com.leverx.blog.exception.ForbiddenAccessException;
import com.leverx.blog.exception.NotFoundException;
import com.leverx.blog.exception.NotModifiedException;
import com.leverx.blog.exception.ValidationException;
import com.leverx.blog.model.Article;
import com.leverx.blog.model.Comment;
import com.leverx.blog.repository.ArticleRepository;
import com.leverx.blog.repository.CommentRepository;
import com.leverx.blog.repository.UserRepository;
import com.leverx.blog.service.CommentService;
import com.leverx.blog.service.validation.BlogValidator;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = Logger.getLogger(ArticleServiceImpl.class);

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              ArticleRepository articleRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Comment findCommentById(int commentId, int articleId) {
        return commentRepository.findByIdAndArticle_Id(commentId, articleId)
                .orElseThrow(() -> new NotFoundException("comment not found"));
    }

    @Override
    @Transactional
    public void addComment(Comment comment, int articleId) {
        if (!BlogValidator.isCorrect(comment)) {
            logger.error("Error saving comment into DB: validation failed");
            throw new ValidationException("Incorrect data!");
        }
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        comment.setUser(userRepository.findByEmail(email));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("article not found"));
        comment.setArticle(article);

        if (null == commentRepository.save(comment)) {
            logger.error("Error saving comment into DB: not modified");
            throw new NotModifiedException("Error: data havent been modified");
        }
    }

    @Override
    @Transactional
    public void deleteCommentById(int commentId, int articleId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment comment = commentRepository.findByIdAndArticle_Id(commentId, articleId)
                .orElseThrow(() -> new NotFoundException("comment not found"));

        if (!comment.getUser().getEmail().equals(email)) {
            logger.error("Error deleting comment: access denied");
            throw new ForbiddenAccessException("Access denied");
        }
        commentRepository.deleteByIdAndArticle_Id(commentId, articleId);
        if (commentRepository.existsById(commentId)) {
            logger.error("Error deleting comment: not modified");
            throw new NotModifiedException("Error: comment havent been deleted");
        }
    }

    @Override
    @Transactional
    public Page<Comment> findArticlesComments(int articleId, Pageable pageable) {
        return commentRepository.findByArticle_Id(articleId, pageable);
    }

}
