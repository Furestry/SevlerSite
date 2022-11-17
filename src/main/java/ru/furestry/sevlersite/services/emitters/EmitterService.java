package ru.furestry.sevlersite.services.emitters;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.furestry.sevlersite.repositories.interfaces.EmitterRepository;

public abstract class EmitterService {
    private final long eventsTimeout = -1L;
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
