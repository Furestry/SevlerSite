package ru.furestry.sevlersite.repositories.interfaces;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;


public interface EmitterRepository {

    void addOrReplaceEmitter(long userId, SseEmitter emitter);

    void remove(long userId);

    Optional<SseEmitter> get(long userId);

    List<Long> getAllIds();

    long createUniqueId();
}