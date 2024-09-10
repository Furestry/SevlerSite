package ru.furestry.sevlersite.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.furestry.sevlersite.repositories.interfaces.EmitterRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class UsersInMemoryRepository implements EmitterRepository {

    private long previousTimeMillis = System.currentTimeMillis();
    private long counter = 0L;

    private final Map<Long, SseEmitter> userEmitterMap = new ConcurrentHashMap<>();

    @Override
    public void addOrReplaceEmitter(long memberId, SseEmitter emitter) {
        userEmitterMap.put(memberId, emitter);
    }

    @Override
    public void remove(long memberId) {
        userEmitterMap.remove(memberId);
    }

    @Override
    public Optional<SseEmitter> get(long memberId) {
        return Optional.ofNullable(userEmitterMap.get(memberId));
    }

    @Override
    public List<Long> getAllIds() {
        return userEmitterMap.keySet()
                .stream()
                .toList();
    }

    @Override
    public long createUniqueId() {
        long currentTimeMillis = System.currentTimeMillis();

        counter = (currentTimeMillis == previousTimeMillis) ? (counter + 1L) & 1048575L : 0L;

        previousTimeMillis = currentTimeMillis;

        long timeComponent = (currentTimeMillis & 8796093022207L) << 20;

        return timeComponent | counter;
    }

}
