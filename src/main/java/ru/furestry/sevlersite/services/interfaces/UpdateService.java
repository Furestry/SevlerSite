package ru.furestry.sevlersite.services.interfaces;

import ru.furestry.sevlersite.entities.EventDto;

public interface UpdateService {

    void sendUpdate(long userId, EventDto event);

}
