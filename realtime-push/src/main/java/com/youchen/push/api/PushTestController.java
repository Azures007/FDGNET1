package com.youchen.push.api;

import com.youchen.push.PushMessage;
import com.youchen.push.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushTestController {

    private final PushService pushService;

    @PostMapping("/group")
    public ResponseEntity<Void> pushToGroup(@RequestBody Map<String, Object> body) {
        String base = (String) body.get("base");
        String line = (String) body.get("line");
        Integer clazz = (Integer) body.get("clazz");
        PushMessage message = PushMessage.builder()
                .type((String) body.getOrDefault("type", "info"))
                .title((String) body.getOrDefault("title", "通知"))
                .content((String) body.getOrDefault("content", ""))

                .baseId(base)
                .lineId(line)
                .classId(clazz != null ? Integer.valueOf(clazz) : null)
                .build();
        pushService.pushToGroup(message, base, line, clazz);
        return ResponseEntity.ok().build();
    }
}


