package com.lostandfound.portal.repository;

import com.lostandfound.portal.entity.Category;
import com.lostandfound.portal.entity.ItemStatus;
import com.lostandfound.portal.entity.ItemType;
import com.lostandfound.portal.entity.LostItem;
import com.lostandfound.portal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {

    Page<LostItem> findByTypeAndStatus(ItemType type, ItemStatus status, Pageable pageable);

    Page<LostItem> findByTypeAndStatusAndCategory(
            ItemType type, ItemStatus status, Category category, Pageable pageable);

    Page<LostItem> findByReportedBy(User reportedBy, Pageable pageable);

    @Query("""
    SELECT i FROM LostItem i
    WHERE i.type = :type
    AND i.status = :status
    AND (:category IS NULL OR i.category = :category)
    AND (
        LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
    Page<LostItem> searchItems(
        @Param("type") ItemType type,
        @Param("status") ItemStatus status,
        @Param("category") Category category,
        @Param("keyword") String keyword,
        Pageable pageable
);
}
