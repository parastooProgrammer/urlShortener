package com.example.urlshortener.service;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final long CACHE_TTL = 1; // hours

    public String shortenUrl(String originalUrl) {
        // Check cache
        String cached = redisTemplate.opsForValue().get(originalUrl);
        if (cached != null) {
            return cached;
        }

        // Check DB
        Optional<Url> existing = urlRepository.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) {
            String shortened = existing.get().getShortenedUrl();
            redisTemplate.opsForValue().set(originalUrl, shortened, CACHE_TTL, TimeUnit.HOURS);
            return shortened;
        }

        // Generate new short code
        String shortCode = generateShortCode();

        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortenedUrl(shortCode);
        urlRepository.save(url);

        // Cache both ways
        redisTemplate.opsForValue().set(originalUrl, shortCode, CACHE_TTL, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(shortCode, originalUrl, CACHE_TTL, TimeUnit.HOURS);

        return shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        // Check cache
        String cached = redisTemplate.opsForValue().get(shortCode);
        if (cached != null) {
            return cached;
        }

        // Check DB
        Optional<Url> existing = urlRepository.findByShortenedUrl(shortCode);
        return existing.map(Url::getOriginalUrl).orElse(null);
    }

    private String generateShortCode() {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = uuid.toString().getBytes(StandardCharsets.UTF_8);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, 8);
    }


}
