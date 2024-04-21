package com.ThreadWeaver.prototype.controller;

import com.ThreadWeaver.prototype.dto.SearchDTO;
import com.ThreadWeaver.prototype.model.FileMetadata;
import com.ThreadWeaver.prototype.model.SizeRequest;
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

import java.util.ArrayList;
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
            List<SearchDTO> searchDTOList = new ArrayList<>();
            for (FileMetadata fileMetadata : searchResults) {
                SearchDTO searchDTO = getSearchDTO(fileMetadata);
                searchDTOList.add(searchDTO);
            }
            return ResponseEntity.ok().body(searchDTOList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private static SearchDTO getSearchDTO(FileMetadata fileMetadata) {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setId(fileMetadata.getId());
        searchDTO.setFilename(fileMetadata.getFilename());
        searchDTO.setChecksum(fileMetadata.getChecksum());
        searchDTO.setDescription(fileMetadata.getDescription());
        searchDTO.setFileSize(fileMetadata.getFileSize());
        searchDTO.setFileType(fileMetadata.getFileType());
        searchDTO.setFileExtension(fileMetadata.getFileExtension());
        return searchDTO;
    }

    @PostMapping("/type")
    public ResponseEntity<?> searchByType(@RequestBody List<String> types) {
        try {
            searchService.setStrategy(new TypeSearchStrategy(types));
            List<FileMetadata> searchResults = searchService.searchFiles();
            List<SearchDTO> searchDTOList = new ArrayList<>();
            for (FileMetadata fileMetadata : searchResults) {
                SearchDTO searchDTO = getSearchDTO(fileMetadata);
                searchDTOList.add(searchDTO);
            }
            return ResponseEntity.ok().body(searchDTOList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/size")
    public ResponseEntity<?> searchBySize(@RequestBody SizeRequest sizeRequest) {
        try {
            searchService.setStrategy(new SizeSearchStrategy(sizeRequest.getMinSize(), sizeRequest.getMaxSize()));
            List<FileMetadata> searchResults = searchService.searchFiles();
            List<SearchDTO> searchDTOList = new ArrayList<>();
            for (FileMetadata fileMetadata : searchResults) {
                SearchDTO searchDTO = getSearchDTO(fileMetadata);
                searchDTOList.add(searchDTO);
            }
            return ResponseEntity.ok().body(searchDTOList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}