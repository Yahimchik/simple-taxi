package com.simple.taxi.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.taxi.auth.model.dto.LoginContext;
import com.simple.taxi.auth.model.enums.BlockType;
import com.simple.taxi.auth.service.LoginAttemptService;
import lombok.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.simple.taxi.auth.model.enums.BlockType.*;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    public static final int MAX_ATTEMPT_LIMIT = 10;
    private static final int CAPTCHA_LIMIT = 3;
    private static final Duration IP_BLOCK_DURATION = Duration.ofHours(24);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void loginSucceeded(LoginContext ctx) {
        clearAttempts(ctx.identifier(), ctx.deviceId(), ctx.clientIp());
    }

    @Override
    public void loginFailed(String email, String deviceId, String deviceInfo, String ip) {
        Attempt emailAttempt = incrementAttempt(buildEmailKey(email), email, deviceInfo);
        Attempt deviceAttempt = incrementAttempt(buildDeviceKey(email, deviceId), email, deviceInfo);

        if (ip != null) {
            Attempt ipAttempt = incrementAttempt(buildIpKey(ip), email, deviceInfo);

            if (emailAttempt.getCount() >= MAX_ATTEMPT_LIMIT || deviceAttempt.getCount() >= MAX_ATTEMPT_LIMIT || ipAttempt.getCount() >= MAX_ATTEMPT_LIMIT) {
                redisTemplate.opsForValue().set(buildIpBlockKey(ip), "BLOCKED", IP_BLOCK_DURATION);
                clearAttempts(email, deviceId, ip);
            }
        }
    }

    @Override
    public boolean isCaptchaRequired(LoginContext context) {
        return isAttemptsLimitReached(context.identifier(), context.deviceId(), context.clientIp(), CAPTCHA_LIMIT);
    }

    @Override
    public boolean isBlocked(LoginContext ctx) {
        boolean ipHardBlocked = ctx.clientIp() != null && redisTemplate.hasKey(buildIpBlockKey(ctx.clientIp()));
        if (ipHardBlocked) return true;

        return isAttemptsLimitReached(ctx.identifier(), ctx.deviceId(), ctx.clientIp(), MAX_ATTEMPT_LIMIT);
    }

    @Override
    public String getBlockRemainingReadable(String ip) {
        Long ttl = redisTemplate.getExpire(buildIpBlockKey(ip));
        return convertToHumanType(ttl);
    }


    @Override
    public AttemptStatus getStatus(String email, String deviceId, String ip) {
        LoginContext ctx = new LoginContext(email, deviceId, ip, null);
        return new AttemptStatus(
                isCaptchaRequired(ctx),
                isBlocked(ctx),
                getAttemptsCount(email, deviceId, ip, EMAIL),
                getAttemptsCount(email, deviceId, ip, DEVICE),
                getAttemptsCount(email, deviceId, ip, IP_ADDRESS),
                getBlockRemainingReadable(ip)
        );
    }

    @Override
    public void clearAttempts(String email, String deviceId, String ip) {
        if (email != null) {
            redisTemplate.delete(buildEmailKey(email));
        }
        if (deviceId != null) {
            redisTemplate.delete(buildDeviceKey(email, deviceId));
        }
        if (ip != null) {
            redisTemplate.delete(buildIpBlockKey(ip));
            redisTemplate.delete(buildIpKey(ip));
        }
    }

    private Attempt incrementAttempt(String redisKey, String email, String deviceInfo) {
        Attempt attempt = getAttemptFromRedis(redisKey);
        if (attempt == null) {
            attempt = new Attempt(1, LocalDateTime.now(), LocalDateTime.now(), null, email, deviceInfo);
        } else {
            attempt.setCount(attempt.getCount() + 1);
            attempt.setLastAttemptTime(LocalDateTime.now());
        }

        saveAttempt(redisKey, attempt);
        return attempt;
    }

    private boolean isAttemptsLimitReached(String email, String deviceId, String ip, int limit) {
        Attempt emailAttempt = getAttemptFromRedis(buildEmailKey(email));
        Attempt deviceAttempt = getAttemptFromRedis(buildDeviceKey(email, deviceId));
        Attempt ipAttempt = ip == null ? null : getAttemptFromRedis(buildIpKey(ip));

        return (emailAttempt != null && emailAttempt.getCount() > limit)
                || (deviceAttempt != null && deviceAttempt.getCount() > limit)
                || (ipAttempt != null && ipAttempt.getCount() > limit);
    }

    private void saveAttempt(String redisKey, Attempt attempt) {
        try {
            redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(attempt), IP_BLOCK_DURATION);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Attempt getAttemptFromRedis(String redisKey) {
        String value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) return null;
        try {
            return objectMapper.readValue(value, Attempt.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private int getAttemptsCount(String email, String deviceId, String ip, BlockType type) {
        Attempt attempt = switch (type) {
            case EMAIL -> getAttemptFromRedis(buildEmailKey(email));
            case DEVICE -> getAttemptFromRedis(buildDeviceKey(email, deviceId));
            case IP_ADDRESS -> getAttemptFromRedis(buildIpKey(ip));
        };
        return attempt == null ? 0 : attempt.getCount();
    }

    private String buildDeviceKey(String email, String deviceId) {
        return "login:attempts:email:" + email + ":deviceId:" + deviceId;
    }

    private String buildEmailKey(String email) {
        return "login:attempts:email:" + email;
    }

    private String buildIpKey(String ip) {
        return "login:attempts:ip:" + ip;
    }

    private String buildIpBlockKey(String ip) {
        return "login:blocked:ip:" + ip;
    }

    private String convertToHumanType(Long remainingSeconds) {
        if (remainingSeconds == null || remainingSeconds <= 0) return null;

        long hours = remainingSeconds / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");

        sb.append(seconds).append("s");
        return sb.toString().trim();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Attempt {
        private int count;
        private LocalDateTime firstAttemptTime;
        private LocalDateTime lastAttemptTime;
        private String email;
        private String deviceId;
        private String deviceInfo;
    }

    public record AttemptStatus(
            boolean captchaRequired,
            boolean blocked,
            int emailAttempts,
            int deviceAttempts,
            int ipAttempts,
            String ipBlockRemaining
    ) {
    }
}