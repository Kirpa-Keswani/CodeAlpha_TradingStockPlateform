import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

class Portfolio {
    private String userId;
    private Map<String, Integer> holdings; // symbol -> quantity
    private Map<String, BigDecimal> avgCosts; // symbol -> average cost per share
    private BigDecimal cash;
    private List<Transaction> transactions;
    
    public Portfolio(String userId, BigDecimal initialCash) {
        this.userId = userId;
        this.holdings = new HashMap<>();
        this.avgCosts = new HashMap<>();
        this.cash = initialCash;
        this.transactions = new ArrayList<>();
    }
    
    public boolean buyStock(Stock stock, int quantity) {
        BigDecimal totalCost = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        
        if (cash.compareTo(totalCost) < 0) {
            return false; // Insufficient funds
        }
        
        cash = cash.subtract(totalCost);
        
        String symbol = stock.getSymbol();
        int currentQuantity = holdings.getOrDefault(symbol, 0);
        BigDecimal currentAvgCost = avgCosts.getOrDefault(symbol, BigDecimal.ZERO);
        
        // Calculate new average cost
        BigDecimal totalCurrentValue = currentAvgCost.multiply(BigDecimal.valueOf(currentQuantity));
        BigDecimal newTotalValue = totalCurrentValue.add(totalCost);
        int newQuantity = currentQuantity + quantity;
        
        BigDecimal newAvgCost = newTotalValue.divide(BigDecimal.valueOf(newQuantity), 
                                                   2, RoundingMode.HALF_UP);
        
        holdings.put(symbol, newQuantity);
        avgCosts.put(symbol, newAvgCost);
        
        // Record transaction
        String transactionId = "TXN" + System.currentTimeMillis();
        Transaction transaction = new Transaction(transactionId, symbol, 
                                                Transaction.Type.BUY, quantity, 
                                                stock.getCurrentPrice());
        transactions.add(transaction);
        
        return true;
    }
    
    public boolean sellStock(Stock stock, int quantity) {
        String symbol = stock.getSymbol();
        int currentQuantity = holdings.getOrDefault(symbol, 0);
        
        if (currentQuantity < quantity) {
            return false; // Insufficient shares
        }
        
        BigDecimal totalValue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        cash = cash.add(totalValue);
        
        int newQuantity = currentQuantity - quantity;
        if (newQuantity == 0) {
            holdings.remove(symbol);
            avgCosts.remove(symbol);
        } else {
            holdings.put(symbol, newQuantity);
        }
        
        // Record transaction
        String transactionId = "TXN" + System.currentTimeMillis();
        Transaction transaction = new Transaction(transactionId, symbol, 
                                                Transaction.Type.SELL, quantity, 
                                                stock.getCurrentPrice());
        transactions.add(transaction);
        
        return true;
    }
    
    public BigDecimal getPortfolioValue(Map<String, Stock> stocks) {
        BigDecimal totalValue = cash;
        
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            Stock stock = stocks.get(symbol);
            
            if (stock != null) {
                BigDecimal stockValue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
                totalValue = totalValue.add(stockValue);
            }
        }
        
        return totalValue;
    }
    
    public BigDecimal getStockValue(String symbol, Stock stock) {
        int quantity = holdings.getOrDefault(symbol, 0);
        return stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
    }
    
    public BigDecimal getGainLoss(String symbol, Stock stock) {
        int quantity = holdings.getOrDefault(symbol, 0);
        if (quantity == 0) return BigDecimal.ZERO;
        
        BigDecimal currentValue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        BigDecimal costBasis = avgCosts.get(symbol).multiply(BigDecimal.valueOf(quantity));
        
        return currentValue.subtract(costBasis);
    }
    
    // Getters
    public String getUserId() { return userId; }
    public Map<String, Integer> getHoldings() { return new HashMap<>(holdings); }
    public BigDecimal getCash() { return cash; }
    public List<Transaction> getTransactions() { return new ArrayList<>(transactions); }
    public BigDecimal getAvgCost(String symbol) { return avgCosts.getOrDefault(symbol, BigDecimal.ZERO); }
}
