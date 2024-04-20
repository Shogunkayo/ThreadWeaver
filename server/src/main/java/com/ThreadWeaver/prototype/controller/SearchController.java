package com.ThreadWeaver.prototype.controller;

import com.ThreadWeaver.prototype.model.FileMetadata;
import com.ThreadWeaver.prototype.service.SearchService;
import com.ThreadWeaver.prototype.service.strategies.KeywordSearchStrategy;
import com.ThreadWeaver.prototype.service.strategies.SizeSearchStrategy;
import com.ThreadWeaver.prototype.service.strategies.TypeSearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("/keyword")
    public ResponseEntity<?> searchByKeyword(@RequestBody String keyword) {
        try {
            searchService.setStrategy(new KeywordSearchStrategy(keyword));
            List<FileMetadata> searchResults = searchService.searchFiles();
            return ResponseEntity.ok().body(searchResults);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/type")
    public ResponseEntity<?> searchByType(@RequestBody List<String> types) {
        try {
            searchService.setStrategy(new TypeSearchStrategy(types));
            List<FileMetadata> searchResults = searchService.searchFiles();
            return ResponseEntity.ok().body(searchResults);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/size")
    public ResponseEntity<?> searchBySize(@RequestBody Long minSize, Long maxSize) {
        try {
            searchService.setStrategy(new SizeSearchStrategy(minSize, maxSize));
            List<FileMetadata> searchResults = searchService.searchFiles();
            return ResponseEntity.ok().body(searchResults);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}