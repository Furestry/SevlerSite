package ru.furestry.sevlersite.services.interfaces;

import ru.furestry.sevlersite.entities.EventDto;

public interface INotificationService {

    void sendUpdate(long userId, EventDto event);

}
