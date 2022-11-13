package ru.furestry.sevlersite.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.furestry.sevlersite.repositories.interfaces.EmitterRepository;

@Service
public class EmitterService {
    private final long eventsTimeout = 1000 * 60 * 5;
    private final EmitterRepository repository;

    public EmitterService(EmitterRepository repository) {
        this.repository = repository;
    }

    public SseEmitter createEmitter(long userId) {
        SseEmitter emitter = new SseEmitter(eventsTimeout);

        emitter.onCompletion(() -> repository.remove(userId));
        emitter.onTimeout(() -> repository.remove(userId));
        emitter.onError(e -> repository.remove(userId));

        repository.addOrReplaceEmitter(userId, emitter);

        return emitter;
    }

    public SseEmitter createEmitter() {
        return createEmitter(repository.createUniqueId());
    }

}
