package com.leverx.blog.service;

import com.leverx.blog.DTO.TagCloud;
import com.leverx.blog.model.Article;

import java.util.List;
import java.util.Set;

public interface TagService {

    Set<Article> findArticlesByTags(String[] tagNames);

    List<TagCloud> tagsCloud();

}
