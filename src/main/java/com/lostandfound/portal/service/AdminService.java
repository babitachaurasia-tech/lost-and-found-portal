package com.lostandfound.portal.service;

import com.lostandfound.portal.dto.response.AdminUserResponse;
import com.lostandfound.portal.dto.response.ItemResponse;
import com.lostandfound.portal.dto.response.ReporterResponse;
import com.lostandfound.portal.entity.LostItem;
import com.lostandfound.portal.entity.User;
import com.lostandfound.portal.exception.ResourceNotFoundException;
import com.lostandfound.portal.repository.LostItemRepository;
import com.lostandfound.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;

    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToAdminUserResponse);
    }

    public Page<ItemResponse> getAllItems(Pageable pageable) {
        return lostItemRepository.findAll(pageable).map(this::mapToItemResponse);
    }

    public void deleteItem(Long itemId) {
        if (!lostItemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item not found with id: " + itemId);
        }
        lostItemRepository.deleteById(itemId);
    }

    private AdminUserResponse mapToAdminUserResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private ItemResponse mapToItemResponse(LostItem item) {
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
                .imageUrl(item.getImageUrl())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
