package ru.furestry.sevlersite.services.notifications;

import org.springframework.stereotype.Service;
import ru.furestry.sevlersite.components.EventMapper;
import ru.furestry.sevlersite.repositories.UsersInMemoryRepository;

@Service
public class UsersNotificationService extends NotificationService {

    public UsersNotificationService(UsersInMemoryRepository emitterRepository, EventMapper eventMapper) {
        super(emitterRepository, eventMapper);
    }

}
