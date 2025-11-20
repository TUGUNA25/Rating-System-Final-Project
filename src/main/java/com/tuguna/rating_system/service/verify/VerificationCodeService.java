package com.tuguna.rating_system.service.verify;

import com.tuguna.rating_system.model.entity.User;
import com.tuguna.rating_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public String generateAndStoreCode(Long userId) {
        String code = UUID.randomUUID().toString();

        String key = "emailVerification:" + code;

        // store code with 24hour
        redisTemplate.opsForValue().set(key, userId.toString(), 24, TimeUnit.HOURS);

        return code;
    }

    public Long getUserIdByCode(String code) {
        String key = "emailVerification:" + code;
        String userId = redisTemplate.opsForValue().get(key);

        if (userId == null) {
            return null;
        }

        return Long.valueOf(userId);
    }

    public void deleteCode(String code) {
        String key = "emailVerification:" + code;
        redisTemplate.delete(key);
    }


    @Transactional
    public String confirmEmail(String code) {
        Long userId = getUserIdByCode(code);

        if (userId == null) return "Invalid or expired";

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailVerified(true);

        deleteCode(code);

        return "Email confirmed!";
    }


    public String generateResetCode(Long userId) {
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        String key = "passwordReset:" + code;
        redisTemplate.opsForValue().set(key, userId.toString(), 10, TimeUnit.MINUTES);
        return code;
    }

    public Long getUserIdByResetCode(String code) {
        String key = "passwordReset:" + code;
        String userId = redisTemplate.opsForValue().get(key);
        return userId == null ? null : Long.valueOf(userId);
    }

    public void deleteResetCode(String code) {
        redisTemplate.delete("passwordReset:" + code);
    }



}