// PasswordResetTokenService
@Service
@Transactional
public class PasswordResetTokenService {
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    public String generateResetToken(User user) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);
        
        // Generate new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        
        tokenRepository.save(resetToken);
        return token;
    }
    
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        
        if (resetToken.isEmpty()) {
            return false;
        }
        
        PasswordResetToken tokenEntity = resetToken.get();
        
        // Check if token is expired or already used
        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now()) || tokenEntity.isUsed()) {
            return false;
        }
        
        return true;
    }
    
    public User getUserByToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        
        if (resetToken.isEmpty()) {
            throw new RuntimeException("Invalid token");
        }
        
        return resetToken.get().getUser();
    }
    
    public void invalidateToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        
        if (resetToken.isPresent()) {
            PasswordResetToken tokenEntity = resetToken.get();
            tokenEntity.setUsed(true);
            tokenRepository.save(tokenEntity);
        }
    }
    
    // Clean up expired tokens (call this periodically)
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
