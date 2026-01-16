package com.infy.icinema.controller;

import com.infy.icinema.dto.MovieDTO;
import com.infy.icinema.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(origins = "*") // Configure properly in production
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    // Helper to get User ID from Security Context
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Assuming SecurityConfig sets the principal as user ID or details containing
        // ID.
        // For simplicity adapting to existing Auth logic or assuming Header-based logic
        // for now if JWT not fully integrated in context
        // But since we use UserDetailsServiceImpl, principal is likely UserDetails.
        // Quick fallback: If using CustomUserDetailsService returning CustomUserDetails
        if (auth != null && auth.getPrincipal() instanceof com.infy.icinema.security.CustomUserDetails) {
            return ((com.infy.icinema.security.CustomUserDetails) auth.getPrincipal()).getId();
        }
        throw new RuntimeException("User not authenticated");
    }

    @PostMapping("/toggle/{movieId}")
    public ResponseEntity<?> toggleWatchlist(@PathVariable Long movieId) {
        watchlistService.toggleWatchlist(getCurrentUserId(), movieId);
        return ResponseEntity.ok(Map.of("message", "Watchlist updated"));
    }

    @GetMapping
    public ResponseEntity<List<MovieDTO>> getMyWatchlist() {
        return ResponseEntity.ok(watchlistService.getUserWatchlist(getCurrentUserId()));
    }

    @GetMapping("/check/{movieId}")
    public ResponseEntity<Boolean> checkWatchlistStatus(@PathVariable Long movieId) {
        return ResponseEntity.ok(watchlistService.isWatchlisted(getCurrentUserId(), movieId));
    }
}
