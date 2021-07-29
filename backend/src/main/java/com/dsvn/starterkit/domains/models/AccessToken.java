package com.dsvn.starterkit.domains.models;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken {
    private Long id;

    private Long userId;

    private String token;

    private LocalDateTime expiresAt;

    private Boolean locked;

    public boolean isNonExpired() {
        return expiresAt.isAfter(LocalDateTime.now());
    }

    public boolean isNonLocked() {
        return !this.locked;
    }
}
