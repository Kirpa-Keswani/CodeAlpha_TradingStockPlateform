import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

class MarketData {
    private Map<String, Stock> stocks;
    private Timer priceUpdateTimer;
    
    public MarketData() {
        stocks = new HashMap<>();
        initializeStocks();
        startPriceUpdates();
    }
    
    private void initializeStocks() {
        stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", new BigDecimal("150.00")));
        stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", new BigDecimal("2500.00")));
        stocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", new BigDecimal("300.00")));
        stocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", new BigDecimal("3200.00")));
        stocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", new BigDecimal("800.00")));
        stocks.put("META", new Stock("META", "Meta Platforms Inc.", new BigDecimal("320.00")));
        stocks.put("NVDA", new Stock("NVDA", "NVIDIA Corp.", new BigDecimal("220.00")));
        stocks.put("NFLX", new Stock("NFLX", "Netflix Inc.", new BigDecimal("450.00")));
    }
    
    private void startPriceUpdates() {
        priceUpdateTimer = new Timer(true);
        priceUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateAllPrices();
            }
        }, 0, 5000); // Update every 5 seconds
    }
    
    private void updateAllPrices() {
        for (Stock stock : stocks.values()) {
            stock.updatePrice();
        }
    }
    
    public Stock getStock(String symbol) {
        return stocks.get(symbol.toUpperCase());
    }
    
    public Map<String, Stock> getAllStocks() {
        return new HashMap<>(stocks);
    }
    
    public void stopPriceUpdates() {
        if (priceUpdateTimer != null) {
            priceUpdateTimer.cancel();
        }
    }
}
