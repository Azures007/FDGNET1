package com.youchen.push.api;

import com.youchen.push.service.MessageCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message-center")
@RequiredArgsConstructor
public class MessageCenterController {

    private final MessageCenterService service;

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> unreadCount(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(service.unreadCount(userId));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(service.list(userId));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Void> markAllRead(@RequestParam("userId") String userId) {
        service.markAllRead(userId);
        return ResponseEntity.ok().build();
    }
}


