package com.lostandfound.portal.controller;

import com.lostandfound.portal.dto.request.CreateItemRequest;
import com.lostandfound.portal.dto.response.ItemResponse;
import com.lostandfound.portal.entity.Category;
import com.lostandfound.portal.entity.ItemStatus;
import com.lostandfound.portal.entity.ItemType;
import com.lostandfound.portal.entity.User;
import com.lostandfound.portal.repository.UserRepository;
import com.lostandfound.portal.service.LostItemService;
import com.lostandfound.portal.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class LostItemController {

    private final LostItemService lostItemService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(
            @Valid @RequestBody CreateItemRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();
        ItemResponse response = lostItemService.createItem(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> browseItems(
            @RequestParam ItemType type,
            @RequestParam(required = false) Category category,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<ItemResponse> items = lostItemService.browseItems(type, category, pageable);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ItemResponse> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        User currentUser = SecurityUtil.getCurrentUser();
        ItemResponse response = lostItemService.attachImage(id, file, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
public ResponseEntity<Page<ItemResponse>> searchItems(
        @RequestParam ItemType type,
        @RequestParam(required = false) Category category,
        @RequestParam String keyword,
        @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

    Page<ItemResponse> results = lostItemService.searchItems(type, category, keyword, pageable);
    return ResponseEntity.ok(results);
}

    @PatchMapping("/{id}/status")
    public ResponseEntity<ItemResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ItemStatus status) {

        User currentUser = SecurityUtil.getCurrentUser();
        ItemResponse response = lostItemService.updateStatus(id, status, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-listings")
    public ResponseEntity<Page<ItemResponse>> getMyListings(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        User currentUser = SecurityUtil.getCurrentUser();
        Page<ItemResponse> items = lostItemService.getMyListings(currentUser, pageable);
        return ResponseEntity.ok(items);
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
