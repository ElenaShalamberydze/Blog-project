package com.leverx.blog.controller;

import com.leverx.blog.service.TagService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@CrossOrigin
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "/articles/tags")
    public ModelAndView findArticlesByTags(@RequestParam(name = "tags") String tags, ModelAndView model) {
        model.addObject("articles", tagService.findArticlesByTags(tags.split(",")));
        model.setViewName("articles-tags");
        return model;
    }

    @GetMapping(value = "/tags-cloud")
    public ModelAndView tagsCloud(ModelAndView model) {
        model.addObject("tagsCloud", tagService.tagsCloud());
        model.setViewName("tags-cloud");
        return model;
    }

}
