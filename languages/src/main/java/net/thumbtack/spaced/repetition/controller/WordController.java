package net.thumbtack.spaced.repetition.controller;

import net.thumbtack.spaced.repetition.dto.response.word.GetWordsForDictionaryResponse;
import net.thumbtack.spaced.repetition.dto.response.word.PronunciationLinkResponse;
import net.thumbtack.spaced.repetition.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class WordController {
    private final WordService wordService;

    @Autowired
    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping("words/dictionary/{id}")
    public ResponseEntity<GetWordsForDictionaryResponse> getWordsForDictionary(
            @PathVariable(value = "id") int id,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "showAll", required = false, defaultValue = "true") boolean showAll,
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter
    ) {
        if (showAll) {
            return ResponseEntity.ok(wordService.getWordsForDictionary(id, page, pageSize, filter));
        } else {
            return ResponseEntity.ok(wordService.getDictionaryWords(id, page, pageSize, filter));
        }
    }

    @GetMapping("words/{word}/pronunciation")
    public ResponseEntity<PronunciationLinkResponse> getWordPronunciationLink(
            @PathVariable(value = "word") String word) {
        return ResponseEntity.ok(wordService.getWordPronunciationLink(word));
    }

}
