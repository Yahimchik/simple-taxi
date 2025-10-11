package com.simple.taxi.user.kafka.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Update;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEntityChangeHandler implements EntityChangeHandler {

    public static final String UPDATED_AT = "updatedAt";
    public static final String ID = "id";
    protected final R2dbcEntityTemplate template;

    protected <T> void applyPartialUpdate(Class<T> entityClass, String idColumn, Object id, Object after, Object before) {
        Map<String, Object> changes = diffObjects(after, before);
        if (changes.isEmpty()) {
            log.debug("⚪ No changes detected for {}", entityClass.getSimpleName());
            return;
        }

        boolean hasUpdatedAt = false;
        try {
            entityClass.getDeclaredField(UPDATED_AT);
            hasUpdatedAt = true;
        } catch (NoSuchFieldException ignored) {
        }

        if (hasUpdatedAt) {
            changes.put(UPDATED_AT, Instant.now());
        }

        template.update(entityClass)
                .matching(query(where(idColumn).is(id)))
                .apply(buildUpdate(changes))
                .doOnSuccess(count -> log.info("🔄 Updated {} record(s) in {}: {}", count, entityClass.getSimpleName(), changes.keySet()))
                .subscribe();
    }

    protected void deleteById(UUID id, Class<?>... entityClasses) {
        for (Class<?> entityClass : entityClasses) {
            template.delete(entityClass)
                    .matching(query(where(ID).is(id)))
                    .all()
                    .doOnSuccess(count -> log.info("🗑️ Deleted {} record(s) from {}", count, entityClass.getSimpleName()))
                    .subscribe();
        }
    }

    private Map<String, Object> diffObjects(Object after, Object before) {
        Map<String, Object> changes = new HashMap<>();
        if (after == null) return changes;

        if (before == null) {
            for (Field field : after.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object afterVal = field.get(after);
                    if (afterVal != null) {
                        changes.put(field.getName(), afterVal);
                    }
                } catch (IllegalAccessException e) {
                    log.error("Error reading field {}", field.getName(), e);
                }
            }
            return changes;
        }

        for (Field field : after.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object afterVal = field.get(after);
                Object beforeVal = field.get(before);

                if (!Objects.equals(afterVal, beforeVal)) {
                    changes.put(field.getName(), afterVal);
                }
            } catch (IllegalAccessException e) {
                log.error("Error comparing field {}", field.getName(), e);
            }
        }
        return changes;
    }

    private Update buildUpdate(Map<String, Object> changes) {
        Update update = null;
        for (Map.Entry<String, Object> entry : changes.entrySet()) {
            if (update == null) {
                update = update(entry.getKey(), entry.getValue());
            } else {
                update = update.set(entry.getKey(), entry.getValue());
            }
        }
        return update;
    }
}