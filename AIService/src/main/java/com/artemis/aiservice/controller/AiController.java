package com.artemis.aiservice.controller;

import com.artemis.aiservice.dto.DecomposeRequest;
import com.artemis.aiservice.dto.TaskSuggestionDto;
import com.artemis.aiservice.service.GeminiService;
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
