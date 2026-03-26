package com.security.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "role_permissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"role_id", "permission_id"})
        }
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_permission_id")
    private Long rolePermissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @PrePersist
    public void prePersist() {
        assignedAt = LocalDateTime.now();
    }
}