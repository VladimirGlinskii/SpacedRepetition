package net.thumbtack.spaced.repetition.controller;

import net.thumbtack.spaced.repetition.dto.response.dictionary.DictionariesDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.word.GetDictionaryWordsDtoResponse;
import net.thumbtack.spaced.repetition.service.DictionaryService;
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
public class DictionaryController {

    private DictionaryService dictionaryService;

    private WordService wordService;

    @Autowired
    public DictionaryController(DictionaryService dictionaryService, WordService wordService) {
        this.dictionaryService = dictionaryService;
        this.wordService = wordService;
    }

    @GetMapping("dictionaries")
    public ResponseEntity<DictionariesDtoResponse> getDictionaries(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter
    ) {
        return ResponseEntity.ok(dictionaryService.getDictionaries(page, pageSize, filter));
    }

    @GetMapping("dictionaries/{id}")
    public ResponseEntity<GetDictionaryWordsDtoResponse> getDictionaryWordsForUser(
            @PathVariable(value = "id") int id,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter) {
        return ResponseEntity.ok(wordService.getDictionaryWordsForUser(id, page, pageSize, filter));
    }


}
