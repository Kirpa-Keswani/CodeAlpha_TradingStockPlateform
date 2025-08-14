import java.math.BigDecimal;
import java.time.LocalDateTime;

class User {
    private String userId;
    private String username;
    private String email;
    private Portfolio portfolio;
    private LocalDateTime createdAt;
    
    public User(String userId, String username, String email, BigDecimal initialCash) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.portfolio = new Portfolio(userId, initialCash);
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Portfolio getPortfolio() { return portfolio; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
