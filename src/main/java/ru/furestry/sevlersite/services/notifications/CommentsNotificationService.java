package ru.furestry.sevlersite.services.notifications;

import org.springframework.stereotype.Service;
import ru.furestry.sevlersite.components.EventMapper;
import ru.furestry.sevlersite.repositories.CommentsInMemoryRepository;

@Service
public class CommentsNotificationService extends NotificationService {

    public CommentsNotificationService(CommentsInMemoryRepository emitterRepository, EventMapper eventMapper) {
        super(emitterRepository, eventMapper);
    }

}
