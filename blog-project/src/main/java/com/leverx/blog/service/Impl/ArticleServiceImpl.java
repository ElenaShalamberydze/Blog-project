package com.leverx.blog.service.Impl;

import com.leverx.blog.exception.ForbiddenAccessException;
import com.leverx.blog.exception.NotFoundException;
import com.leverx.blog.exception.NotModifiedException;
import com.leverx.blog.exception.ValidationException;
import com.leverx.blog.model.Article;
import com.leverx.blog.model.Status;
import com.leverx.blog.model.Tag;
import com.leverx.blog.repository.ArticleRepository;
import com.leverx.blog.repository.TagRepository;
import com.leverx.blog.repository.UserRepository;
import com.leverx.blog.service.ArticleService;
import com.leverx.blog.service.validation.BlogValidator;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger logger = Logger.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository,
                              UserRepository userRepository, TagRepository tagRepository
    ) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public Page<Article> findPublicArticles(Pageable pageable) {
        return articleRepository.findByStatus(Status.PUBLIC, pageable);
    }

    @Override
    @Transactional
    public List<Article> findArticlesByUserEmail(String email) {
        return articleRepository.findByUser_Email(email);
    }

    @Override
    @Transactional
    public void createArticle(Article article, String tagString) {
        if (!BlogValidator.isCorrect(article) || !BlogValidator.tagsCheck(tagString)) {
            logger.error("Error saving article into DB: validation failed");
            throw new ValidationException("Incorrect data!");
        }
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        article.setUser(userRepository.findByEmail(email));
        article.setStatus(Status.PUBLIC);
        article.setCreatedAt(LocalDate.now());

        if (null != tagString && !tagString.isEmpty()) {
            article.setTags(formTagList(tagString));
        }
        if (null == articleRepository.save(article)) {
            logger.error("Error saving article into DB: not modified");
            throw new NotModifiedException("Error: data havent been modified");
        }
    }

    @Override
    @Transactional
    public Article findById(int articleId) {
        return articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("article not found"));
    }

    @Override
    @Transactional
    public void updateArticle(Article article, String tagString) {

        if (!BlogValidator.isCorrect(article) || !BlogValidator.tagsCheck(tagString)) {
            logger.error("Error updating article: validation failed");
            throw new ValidationException("Incorrect data!");
        }
        Article existingArticle =
                articleRepository.findById(article.getId()).orElseThrow(() -> new NotFoundException("article not found"));
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!existingArticle.getUser().getEmail().equals(email)) {
            logger.error("Error updating article: access denied");
            throw new ForbiddenAccessException("Access denied");
        }
        article.setCreatedAt(existingArticle.getCreatedAt());
        article.setUser(existingArticle.getUser());
        article.setUpdatedAt(LocalDate.now());

        if (null != tagString && !tagString.isEmpty()) {
            article.setTags(formTagList(tagString));
        } else {
            article.setTags(existingArticle.getTags());
        }
        if (null == articleRepository.save(article)) {
            logger.error("Error updating article: not modified");
            throw new NotModifiedException("Error: data havent been modified");
        }
    }

    @Override
    @Transactional
    public void deleteArticleById(int articleId) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("article not found"));
        if (!article.getUser().getEmail().equals(email)) {
            logger.error("Error deleting article: access denied");
            throw new ForbiddenAccessException("Access denied");
        }
        articleRepository.deleteByIdAndUser_Email(articleId, email);
        if (articleRepository.existsById(articleId)) {
            logger.error("Error deleting article from DB: not modified");
            throw new NotModifiedException("Error: article havent been deleted");
        }
    }

    @Override
    @Transactional
    public void changeArticleStatus(int articleId, String status) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new NotFoundException("article not found"));
        if (!article.getUser().getEmail().equals(email)) {
            logger.error("Error changing article status: access denied");
            throw new ForbiddenAccessException("Access denied");
        }
        if (!(status.equals("PUBLIC") || status.equals("DRAFT"))) {
            logger.error("Error changing article status: validation failed");
            throw new ValidationException("Incorrect data!");
        }
        articleRepository.updateStatus(status, articleId);
        if (!status.equals(articleRepository.status(articleId))) {
            logger.error("Error changing articles status: not modified");
            throw new NotModifiedException("Error: data havent been modified");
        }
    }

    private List<Tag> formTagList(String tagString) {
        List<Tag> tags = new ArrayList<>();
        String[] tagNames = tagString.replaceAll("\\]", "").replaceAll("\\[", "").split(", ");
        for (String tagName : tagNames) {
            tags.add(Objects.requireNonNullElseGet(tagRepository.findByName(tagName), () -> new Tag(tagName)));
        }
        return tags;
    }

}
