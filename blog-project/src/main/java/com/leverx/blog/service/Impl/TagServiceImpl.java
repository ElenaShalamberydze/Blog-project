package com.leverx.blog.service.Impl;

import com.leverx.blog.DTO.TagCloud;
import com.leverx.blog.model.Article;
import com.leverx.blog.model.Tag;
import com.leverx.blog.repository.TagRepository;
import com.leverx.blog.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public Set<Article> findArticlesByTags(String[] tagNames) {
        Set<Article> articles = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName);
            if (null != tag) {
                articles.addAll(tag.getArticles());
            }
        }
        return articles;
    }

    @Override
    @Transactional
    public List<TagCloud> tagsCloud() {
        return tagRepository.tagsCloud();
    }

}
