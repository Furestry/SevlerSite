package ru.furestry.sevlersite.entities.db;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String text;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime commentedAt;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id" , nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="author_id", referencedColumnName = "id", nullable=false)
    private User author;
}
