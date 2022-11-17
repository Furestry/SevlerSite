package ru.furestry.sevlersite.services.emitters;

import org.springframework.stereotype.Service;
import ru.furestry.sevlersite.repositories.CommentsInMemoryRepository;

@Service
public class CommentEmitterService extends EmitterService {

    public CommentEmitterService(CommentsInMemoryRepository repository) {
        super(repository);
    }

}
