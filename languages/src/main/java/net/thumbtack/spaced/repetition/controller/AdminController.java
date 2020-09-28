package net.thumbtack.spaced.repetition.controller;

import net.thumbtack.spaced.repetition.dto.request.dictionary.AddDictionaryRequest;
import net.thumbtack.spaced.repetition.dto.request.dictionary.RenameDictionaryRequest;
import net.thumbtack.spaced.repetition.dto.request.word.AddWordRequest;
import net.thumbtack.spaced.repetition.dto.request.word.EditWordRequest;
import net.thumbtack.spaced.repetition.dto.response.dictionary.DictionaryDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.user.UsersDtoResponse;
import net.thumbtack.spaced.repetition.dto.response.word.AddWordResponse;
import net.thumbtack.spaced.repetition.dto.response.word.WordsDtoResponse;
import net.thumbtack.spaced.repetition.service.DictionaryService;
import net.thumbtack.spaced.repetition.service.UserService;
import net.thumbtack.spaced.repetition.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/admin/",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class AdminController {

    private UserService userService;

    private WordService wordService;

    private DictionaryService dictionaryService;

    @Autowired
    public AdminController(UserService userService, WordService wordService, DictionaryService dictionaryService) {
        this.userService = userService;
        this.wordService = wordService;
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("users")
    public ResponseEntity<UsersDtoResponse> getAllUsers(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter) {
        return ResponseEntity.ok(userService.getAll(page, pageSize, filter));
    }

    @GetMapping("words")
    public ResponseEntity<WordsDtoResponse> getAllWords(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "pageSize") int pageSize,
            @RequestParam(value = "filter", required = false, defaultValue = "") String filter) {
        return ResponseEntity.ok(wordService.getAll(page, pageSize, filter));
    }

    @PostMapping("words")
    public ResponseEntity<AddWordResponse> addWord(
            @RequestBody @Valid AddWordRequest request,
            @RequestParam(value = "dictionaryId", required = false, defaultValue = "0") int dictionaryId) {
        return ResponseEntity.ok(wordService.addWord(request, dictionaryId));
    }

    @PutMapping("words/{wordId}")
    public ResponseEntity<Void> editWord(
            @PathVariable(value = "wordId") int wordId,
            @RequestBody @Valid EditWordRequest request) {
        wordService.editWord(request, wordId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("words/{wordId}")
    public ResponseEntity<Void> deleteWord(
            @PathVariable(value = "wordId") int wordId) {
        wordService.deleteWord(wordId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("dictionaries")
    public ResponseEntity<DictionaryDtoResponse> addDictionary(
            @RequestBody @Valid AddDictionaryRequest request
    ) {
        return ResponseEntity.ok(dictionaryService.addDictionary(request));
    }

    @PutMapping("dictionaries/{dictionaryId}")
    public ResponseEntity<Void> renameDictionary(
            @PathVariable(value = "dictionaryId") int dictionaryId,
            @RequestBody @Valid RenameDictionaryRequest request) {
        dictionaryService.renameDictionary(dictionaryId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("dictionaries/{dictionaryId}")
    public ResponseEntity<Void> deleteDictionary(
            @PathVariable(value = "dictionaryId") int dictionaryId) {
        dictionaryService.deleteDictionary(dictionaryId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("dictionaries/{dictionaryId}/words/{wordId}")
    public ResponseEntity<Void> addWordToDictionary(
            @PathVariable(value = "dictionaryId") int dictionaryId,
            @PathVariable(value = "wordId") int wordId
    ) {
        dictionaryService.addWord(dictionaryId, wordId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("dictionaries/{dictionaryId}/words/{wordId}")
    public ResponseEntity<Void> deleteWordFromDictionary(
            @PathVariable(value = "dictionaryId") int dictionaryId,
            @PathVariable(value = "wordId") int wordId
    ) {
        dictionaryService.deleteWord(dictionaryId, wordId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable(value = "userId") int userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "words/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadWordsFromFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dictionaryId", required = false, defaultValue = "0") int dictionaryId) {
        wordService.uploadWordsFromFile(file, dictionaryId);
        return ResponseEntity.ok().build();
    }
}
