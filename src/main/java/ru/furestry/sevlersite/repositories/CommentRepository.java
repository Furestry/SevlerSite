package ru.furestry.sevlersite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.furestry.sevlersite.entities.db.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
