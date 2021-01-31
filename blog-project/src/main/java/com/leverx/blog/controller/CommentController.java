package com.leverx.blog.controller;

import com.leverx.blog.exception.ValidationException;
import com.leverx.blog.model.Comment;
import com.leverx.blog.service.ArticleService;
import com.leverx.blog.service.CommentService;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value = "/articles")
public class CommentController {

    private static final Logger logger = Logger.getLogger(CommentController.class);

    private final CommentService commentService;
    private final ArticleService articleService;

    public CommentController(CommentService commentService, ArticleService articleService) {
        this.commentService = commentService;
        this.articleService = articleService;
    }

    @GetMapping(value = "/{id}/comments")
    public ModelAndView findArticleComments(@PathVariable(name = "id") int articleId, ModelAndView model,
                                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Comment> commentPage = commentService.findArticlesComments(articleId, pageable);
        model.addObject("article", articleService.findById(articleId));
        model.addObject("comments", commentPage);
        int totalPages = commentPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addObject("pageNumbers", pageNumbers);
        }

        model.setViewName("article-comments");
        return model;
    }

    @GetMapping(value = "/{id}/comments/{commentId}")
    public ModelAndView findCommentById(@PathVariable(name = "commentId") int commentId,
                                        @PathVariable(name = "id") int articleId,
                                        ModelAndView model
    ) {
        final Comment comment = commentService.findCommentById(commentId, articleId);
        model.addObject("comment", comment);
        model.setViewName("comment");
        return model;
    }

    @PostMapping(value = "/{id}/comments")
    public RedirectView addComment(@PathVariable(name = "id") int articleId, @RequestParam String message) {
        if (null == message) {
            logger.error("Error adding comment: no comment to add");
            throw new ValidationException("Incorrect data!");
        }
        Comment comment = new Comment(message, LocalDate.now());
        commentService.addComment(comment, articleId);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/articles/" + articleId + "/comments");
        return redirectView;
    }

    @PostMapping(value = "/{id}/comments/{commentId}")
    public RedirectView deleteCommentById(@PathVariable(name = "commentId") int commentId,
                                          @PathVariable(name = "id") int articleId
    ) {
        commentService.deleteCommentById(commentId, articleId);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/articles/" + articleId + "/comments");
        return redirectView;
    }

}
