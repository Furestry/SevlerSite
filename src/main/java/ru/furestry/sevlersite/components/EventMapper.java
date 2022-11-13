package ru.furestry.sevlersite.components;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.furestry.sevlersite.entities.EventDto;

import java.util.Random;

@Component
@AllArgsConstructor
public class EventMapper {

    public SseEmitter.SseEventBuilder toSseEventBuilder(EventDto event) {
        Random random = new Random();

        return SseEmitter.event()
                .id(String.valueOf(random.nextInt(999999)))
                .name(event.getType())
                .reconnectTime( 1000 * 60 * 5)
                .data(event.getBody());
    }
}
