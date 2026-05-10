package com.yuranium.aiservice.controller;

import com.yuranium.aiservice.dto.DecomposeRequest;
import com.yuranium.aiservice.dto.TaskSuggestionDto;
import com.yuranium.aiservice.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController
{
    private final GeminiService geminiService;

    @PostMapping("/decompose")
    public ResponseEntity<List<TaskSuggestionDto>> decompose(@RequestBody DecomposeRequest request)
    {
        return new ResponseEntity<>(
                geminiService.decompose(request.goal()),
                HttpStatus.OK
        );
    }
}
