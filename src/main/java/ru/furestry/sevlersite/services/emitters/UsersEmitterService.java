package ru.furestry.sevlersite.services.emitters;

import org.springframework.stereotype.Service;
import ru.furestry.sevlersite.repositories.UsersInMemoryRepository;

@Service
public class UsersEmitterService extends EmitterService {

    public UsersEmitterService(UsersInMemoryRepository repository) {
        super(repository);
    }

}
