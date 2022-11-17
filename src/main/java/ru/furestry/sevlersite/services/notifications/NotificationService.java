package ru.furestry.sevlersite.services.notifications;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import ru.furestry.sevlersite.repositories.interfaces.EmitterRepository;
import ru.furestry.sevlersite.entities.EventDto;
import ru.furestry.sevlersite.components.EventMapper;
import ru.furestry.sevlersite.services.interfaces.INotificationService;

import java.io.IOException;

@Primary
@AllArgsConstructor
public abstract class NotificationService implements INotificationService {

    private final EmitterRepository emitterRepository;
    private final EventMapper eventMapper;

    @Override
    public void sendUpdate(long userId, EventDto event) {
        if (event == null) {
            return;
        }

        doSendUpdate(userId, event);
    }

    private void doSendUpdate(long userId, EventDto event) {
        emitterRepository.get(userId).ifPresent(sseEmitter -> {
            try {
                sseEmitter.send(eventMapper.toSseEventBuilder(event));
            } catch (IOException | IllegalStateException e) {
                emitterRepository.remove(userId);
            }
        });
    }

}
