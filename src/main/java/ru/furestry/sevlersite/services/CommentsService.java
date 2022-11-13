package ru.furestry.sevlersite.services;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.furestry.sevlersite.repositories.interfaces.EmitterRepository;
import ru.furestry.sevlersite.entities.EventDto;
import ru.furestry.sevlersite.components.EventMapper;
import ru.furestry.sevlersite.services.interfaces.UpdateService;

import java.io.IOException;

@Primary
@Service
@AllArgsConstructor
public class CommentsService implements UpdateService {

    private final EmitterRepository emitterRepository;
    private final EventMapper eventMapper;

    @Override
    public void sendUpdate(long userId, EventDto event) {
        if (event == null) {
            return;
        }

        doSendNotification(userId, event);
    }

    private void doSendNotification(long userId, EventDto event) {
        emitterRepository.get(userId).ifPresent(sseEmitter -> {
            try {
                sseEmitter.send(eventMapper.toSseEventBuilder(event));
            } catch (IOException | IllegalStateException e) {
                emitterRepository.remove(userId);
            }
        });
    }

}
