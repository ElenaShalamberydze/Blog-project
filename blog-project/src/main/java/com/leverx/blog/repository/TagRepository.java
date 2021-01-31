package com.leverx.blog.repository;

import com.leverx.blog.DTO.TagCloud;
import com.leverx.blog.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    Tag findByName(String name);

    @Query("SELECT new com.leverx.blog.DTO.TagCloud(t.name, COUNT(DISTINCT a.id)) " +
            "FROM Tag t " +
            "JOIN t.articles a " +
            "GROUP BY t.name")
    List<TagCloud> tagsCloud();

}
