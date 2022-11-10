package ru.furestry.sevlersite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.furestry.sevlersite.entities.db.Privilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(String name);

}
