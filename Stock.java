import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

class Stock {
    private String symbol;
    private String name;
    private BigDecimal currentPrice;
    private BigDecimal previousPrice;
    private LocalDateTime lastUpdated;
    private Random random = new Random();
    
    public Stock(String symbol, String name, BigDecimal initialPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = initialPrice;
        this.previousPrice = initialPrice;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void updatePrice() {
        previousPrice = currentPrice;
        // Simulate price fluctuation (-5% to +5%)
        double change = (random.nextDouble() - 0.5) * 0.1;
        BigDecimal changeAmount = currentPrice.multiply(BigDecimal.valueOf(change));
        currentPrice = currentPrice.add(changeAmount);
        
        // Ensure price doesn't go below $1
        if (currentPrice.compareTo(BigDecimal.ONE) < 0) {
            currentPrice = BigDecimal.ONE;
        }
        
        currentPrice = currentPrice.setScale(2, RoundingMode.HALF_UP);
        lastUpdated = LocalDateTime.now();
    }
    
    public BigDecimal getPriceChange() {
        return currentPrice.subtract(previousPrice);
    }
    
    public BigDecimal getPriceChangePercentage() {
        if (previousPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getPriceChange().divide(previousPrice, 4, RoundingMode.HALF_UP)
               .multiply(BigDecimal.valueOf(100));
    }
    
    // Getters
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public BigDecimal getPreviousPrice() { return previousPrice; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
}