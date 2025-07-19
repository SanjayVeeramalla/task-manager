package com.taskmanager.repository;

import com.taskmanager.model.PasswordResetToken;
import com.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Find a password reset token by its token string
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Delete all tokens associated with a specific user
     */
    void deleteByUser(User user);
    
    /**
     * Delete all expired tokens (cleanup method)
     */
    void deleteByExpiryDateBefore(LocalDateTime now);
    
    /**
     * Find a token by user (useful for checking if user already has a token)
     */
    Optional<PasswordResetToken> findByUser(User user);
}
