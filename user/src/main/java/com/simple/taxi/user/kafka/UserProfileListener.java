package com.simple.taxi.user.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.taxi.user.mapper.UserProfileMapper;
import com.simple.taxi.user.model.dto.UserCreatedEvent;
import com.simple.taxi.user.model.entities.UserProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileListener {

    private final ObjectMapper objectMapper;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final UserProfileMapper userProfileMapper;

    @KafkaListener(topics = "auth.public.users", groupId = "user-service")
    public void listen(String message) {
        try {
            UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);

            if (event.getPayload() == null) return;
            UserCreatedEvent.User user;
            switch (event.getPayload().getOp()) {
                case "c":
                    user = event.getPayload().getAfter();
                    r2dbcEntityTemplate.insert(UserProfile.class)
                            .using(userProfileMapper.toEntity(user))
                            .doOnSuccess(p -> log.info("User profile inserted: {}", p.getUserId()))
                            .subscribe();
                    break;
                case "u":
                    user = event.getPayload().getAfter();
                    r2dbcEntityTemplate.update(UserProfile.class)
                            .matching(query(where("userId").is(user.getId())))
                            .apply(update("phone", user.getPhone()).set("updatedAt", Instant.now()))
                            .doOnSuccess(count -> log.info("Updated {} user profile(s)", count))
                            .subscribe();
                    break;
                case "d":
                    user = event.getPayload().getBefore();
                    r2dbcEntityTemplate.delete(UserProfile.class)
                            .matching(query(where("userId").is(user.getId())))
                            .all()
                            .doOnSuccess(count -> log.info("Deleted {} user profile(s)", count))
                            .subscribe();
                    break;
            }

        } catch (Exception e) {
            log.error("Failed to process user event from Kafka", e);
        }
    }
}