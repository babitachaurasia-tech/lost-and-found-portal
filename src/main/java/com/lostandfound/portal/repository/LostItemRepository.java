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

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {

    Page<LostItem> findByTypeAndStatus(ItemType type, ItemStatus status, Pageable pageable);

    Page<LostItem> findByTypeAndStatusAndCategory(
            ItemType type, ItemStatus status, Category category, Pageable pageable);

    Page<LostItem> findByReportedBy(User reportedBy, Pageable pageable);
}