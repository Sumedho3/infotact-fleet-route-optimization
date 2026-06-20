package com.infotact.fleet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
	
	@Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Immutable record timestamp marking initial generation

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // Mutable timestamp updating on subsequent entry edits

    @PrePersist // 🔄 Lifecycle hook triggered automatically right before an INSERT query fires
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate // 🔄 Lifecycle hook triggered automatically right before an UPDATE query fires
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
