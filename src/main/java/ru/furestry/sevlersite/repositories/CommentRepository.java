package ru.furestry.sevlersite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.furestry.sevlersite.entities.db.Comment;

import java.util.Collection;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Collection<Comment> findAllByAuthorId(Long authorId);

    Collection<Comment> findAllByUserId(Long userId);

}
