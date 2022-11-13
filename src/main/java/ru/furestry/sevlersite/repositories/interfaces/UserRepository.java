package ru.furestry.sevlersite.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.furestry.sevlersite.entities.db.User;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByUsernameIgnoreCase(String username);

    User findByTokenHash(String hash);
}
