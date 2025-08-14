import java.math.BigDecimal;
import java.time.LocalDateTime;

class Transaction {
    public enum Type { BUY, SELL }
    
    private String transactionId;
    private String stockSymbol;
    private Type type;
    private int quantity;
    private BigDecimal price;
    private LocalDateTime timestamp;
    
    public Transaction(String transactionId, String stockSymbol, Type type, 
                      int quantity, BigDecimal price) {
        this.transactionId = transactionId;
        this.stockSymbol = stockSymbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }
    
    public BigDecimal getTotalValue() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters
    public String getTransactionId() { return transactionId; }
    public String getStockSymbol() { return stockSymbol; }
    public Type getType() { return type; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public LocalDateTime getTimestamp() { return timestamp; }
}