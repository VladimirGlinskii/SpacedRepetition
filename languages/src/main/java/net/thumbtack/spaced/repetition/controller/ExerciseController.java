package net.thumbtack.spaced.repetition.controller;

import net.thumbtack.spaced.repetition.dto.request.exercise.CheckAnswerDtoRequest;
import net.thumbtack.spaced.repetition.dto.response.exercise.CheckAnswerDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.exercise.ExerciseDtoResponse;
import net.thumbtack.spaced.repetition.service.ExerciseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("exercises")
    public ResponseEntity<ExerciseDtoResponse> getExercise() {
        return ResponseEntity.ok(exerciseService.getExercise());
    }

    @PutMapping(value = "exercises")
    public ResponseEntity<CheckAnswerDtoResponse> checkAnswer(
            @RequestBody CheckAnswerDtoRequest request) {
        return ResponseEntity.ok(exerciseService.checkAnswer(request));
    }

}
