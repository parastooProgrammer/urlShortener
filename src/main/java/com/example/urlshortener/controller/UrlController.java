package com.example.urlshortener.controller;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String originalUrl) {
        String shortened = urlService.shortenUrl(originalUrl);
        return ResponseEntity.ok(shortened);
    }

    @GetMapping("/{shortenedUrl}")
    public ResponseEntity<Object> redirectToOriginalUrl(@PathVariable String shortenedUrl) {
        String url = urlService.getOriginalUrl(shortenedUrl);
        return ResponseEntity.ok(url);
    }
}
