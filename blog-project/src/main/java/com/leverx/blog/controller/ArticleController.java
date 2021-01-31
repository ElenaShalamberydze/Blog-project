package com.leverx.blog.controller;

import com.leverx.blog.exception.ForbiddenAccessException;
import com.leverx.blog.exception.ValidationException;
import com.leverx.blog.model.Article;
import com.leverx.blog.model.Status;
import com.leverx.blog.model.Tag;
import com.leverx.blog.service.ArticleService;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@CrossOrigin
public class ArticleController {

    private static final Logger logger = Logger.getLogger(ArticleController.class);

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping(value = "/articles")
    public ModelAndView findPublicArticles(ModelAndView model,
                                           @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Article> articlesPage = articleService.findPublicArticles(pageable);
        model.addObject("articles", articleService.findPublicArticles(pageable));
        int totalPages = articlesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addObject("pageNumbers", pageNumbers);
        }
        model.setViewName("articles");
        return model;
    }

    @GetMapping(value = "/my")
    public ModelAndView findArticlesOfUser(ModelAndView model) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addObject("articles", articleService.findArticlesByUserEmail(email));
        model.setViewName("my-articles");
        return model;
    }

    @PostMapping(value = "/articles")
    public RedirectView createArticle(@RequestParam String title,
                                      @RequestParam String text,
                                      @RequestParam(required = false) String tags
    ) {
        if (null == title || null == text) {
            logger.error("Error creating article: empty fields");
            throw new ValidationException("Incorrect data!");
        }
        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            logger.error("Error creating article: forbidden for unauthorized users");
            throw new ForbiddenAccessException("Access denied: unauthorized user");
        }
        articleService.createArticle(new Article(title, text), tags);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/articles");
        return redirectView;
    }

    @GetMapping(value = "/articles/{id}")
    public ModelAndView showUpdateArticle(ModelAndView model, @PathVariable(name = "id") int articleId) {
        Article article = articleService.findById(articleId);
        Set<String> tagNames = new HashSet<>();
        article.getTags().forEach((Tag tag) -> tagNames.add(tag.getName()));
        model.addObject("tags", tagNames.toString());
        model.addObject("article", articleService.findById(articleId));
        model.setViewName("update-article");
        return model;
    }

    @PostMapping(value = "/articles/{id}")
    public RedirectView updateArticle(@PathVariable(name = "id") int articleId,
                                      @RequestParam String title,
                                      @RequestParam String text,
                                      @RequestParam String status,
                                      @RequestParam(required = false) String tags
    ) {
        Article article = new Article(articleId, title, text, Status.valueOf(status.toUpperCase()));
        articleService.updateArticle(article, tags);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/my");
        return redirectView;
    }

    @PostMapping(value = "/articles/{id}/delete")
    public RedirectView deleteArticleById(@PathVariable(name = "id") int articleId) {
        articleService.deleteArticleById(articleId);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/my");
        return redirectView;
    }

    @PostMapping(value = "/articles/{id}/status")
    public RedirectView changeArticleStatus(@PathVariable(name = "id") int articleId, @RequestParam String status) {
        articleService.changeArticleStatus(articleId, status.toUpperCase());
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/my");
        return redirectView;
    }

}
