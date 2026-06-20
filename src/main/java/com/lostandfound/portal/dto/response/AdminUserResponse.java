package com.lostandfound.portal.dto.response;

import com.lostandfound.portal.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}