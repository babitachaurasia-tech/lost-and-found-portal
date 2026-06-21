package com.lostandfound.portal.service;

import com.lostandfound.portal.dto.request.CreateItemRequest;
import com.lostandfound.portal.dto.response.ItemResponse;
import com.lostandfound.portal.dto.response.ReporterResponse;
import com.lostandfound.portal.entity.Category;
import com.lostandfound.portal.entity.ItemStatus;
import com.lostandfound.portal.entity.ItemType;
import com.lostandfound.portal.entity.LostItem;
import com.lostandfound.portal.entity.User;
import com.lostandfound.portal.exception.ResourceNotFoundException;
import com.lostandfound.portal.exception.UnauthorizedActionException;
import com.lostandfound.portal.repository.LostItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LostItemService {

    private final LostItemRepository lostItemRepository;
    private final ImageUploadService imageUploadService;

    public ItemResponse attachImage(Long itemId, MultipartFile file, User currentUser) {

        LostItem item = lostItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (!item.getReportedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not allowed to update this item");
        }

        String imageUrl = imageUploadService.uploadImage(file);
        item.setImageUrl(imageUrl);

        LostItem updated = lostItemRepository.save(item);
        return mapToResponse(updated);
    }

    public ItemResponse createItem(CreateItemRequest request, User currentUser) {

        LostItem item = LostItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .type(request.getType())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .reportedBy(currentUser)
                .build();

        LostItem saved = lostItemRepository.save(item);

        return mapToResponse(saved);
    }

    public Page<ItemResponse> browseItems(
            ItemType type, Category category, Pageable pageable) {

        Page<LostItem> items = (category != null)
                ? lostItemRepository.findByTypeAndStatusAndCategory(
                type, ItemStatus.ACTIVE, category, pageable)
                : lostItemRepository.findByTypeAndStatus(
                type, ItemStatus.ACTIVE, pageable);

        return items.map(this::mapToResponse);
    }

    public Page<ItemResponse> searchItems(
        ItemType type, Category category, String keyword, Pageable pageable) {

    Page<LostItem> items = lostItemRepository.searchItems(
            type, ItemStatus.ACTIVE, category, keyword, pageable);

    return items.map(this::mapToResponse);
}

    public ItemResponse updateStatus(Long itemId, ItemStatus newStatus, User currentUser) {

        LostItem item = lostItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (!item.getReportedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not allowed to update this item");
        }

        item.setStatus(newStatus);
        LostItem updated = lostItemRepository.save(item);

        return mapToResponse(updated);
    }

    public ItemResponse getItemById(Long id) {
    LostItem item = lostItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    return mapToResponse(item);
}

    public Page<ItemResponse> getMyListings(User currentUser, Pageable pageable) {
        return lostItemRepository.findByReportedBy(currentUser, pageable)
                .map(this::mapToResponse);
    }

    private ItemResponse mapToResponse(LostItem item) {
        ReporterResponse reporter = ReporterResponse.builder()
                .id(item.getReportedBy().getId())
                .name(item.getReportedBy().getName())
                .email(item.getReportedBy().getEmail())
                .build();

        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .category(item.getCategory())
                .type(item.getType())
                .location(item.getLocation())
                .eventDate(item.getEventDate())
                .status(item.getStatus())
                .reportedBy(reporter)
                .createdAt(item.getCreatedAt())
                .imageUrl(item.getImageUrl())
                .build();
    }
}
