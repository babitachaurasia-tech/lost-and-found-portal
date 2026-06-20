package com.lostandfound.portal.dto.response;

import com.lostandfound.portal.entity.Category;
import com.lostandfound.portal.entity.ItemStatus;
import com.lostandfound.portal.entity.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {

    private Long id;
    private String title;
    private String description;
    private Category category;
    private ItemType type;
    private String location;
    private LocalDate eventDate;
    private ItemStatus status;
    private ReporterResponse reportedBy;
    private LocalDateTime createdAt;
    private String imageUrl;;
}
