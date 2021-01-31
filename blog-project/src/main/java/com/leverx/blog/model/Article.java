package com.leverx.blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "text")
    private String text;

    @Column(columnDefinition = "ENUM('PUBLIC', 'DRAFT')")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "users_id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "articles_has_tags",
            joinColumns = @JoinColumn(name = "articles_id"),
            inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    private List<Tag> tags;

    @OneToMany(mappedBy = "article",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
                    CascadeType.REFRESH})
    private List<Comment> commentList;

    public Article(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Article(int id, String title, String text, Status status) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.status = status;
    }

}
