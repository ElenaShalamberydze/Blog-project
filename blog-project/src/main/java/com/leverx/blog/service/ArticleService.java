package com.leverx.blog.service;

import com.leverx.blog.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {

    Page<Article> findPublicArticles(Pageable pageable);

    List<Article> findArticlesByUserEmail(String email);

    void createArticle(Article article, String tagString);

    Article findById(int articleId);

    void updateArticle(Article article, String tagString);

    void deleteArticleById(int articleId);

    void changeArticleStatus(int articleId, String status);

}
