import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class TradingPlatform {
    private MarketData marketData;
    private Map<String, User> users;
    private User currentUser;
    private Scanner scanner;
    private DecimalFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    public TradingPlatform() {
        marketData = new MarketData();
        users = FileManager.loadUsers();
        scanner = new Scanner(System.in);
        currencyFormat = new DecimalFormat("$#,##0.00");
        dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    public void start() {
        System.out.println("=== Welcome to Stock Trading Platform ===");
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
    
    private void showLoginMenu() {
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                exit();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. View Market Data");
        System.out.println("2. View Portfolio");
        System.out.println("3. Buy Stock");
        System.out.println("4. Sell Stock");
        System.out.println("5. View Transaction History");
        System.out.println("6. Portfolio Performance");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                viewMarketData();
                break;
            case 2:
                viewPortfolio();
                break;
            case 3:
                buyStock();
                break;
            case 4:
                sellStock();
                break;
            case 5:
                viewTransactionHistory();
                break;
            case 6:
                viewPortfolioPerformance();
                break;
            case 7:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                currentUser = user;
                System.out.println("Welcome back, " + username + "!");
                return;
            }
        }
        
        System.out.println("User not found. Please register first.");
    }
    
    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        // Check if username already exists
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists. Please choose another.");
                return;
            }
        }
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter initial cash amount: $");
        BigDecimal initialCash = getBigDecimalInput();
        
        String userId = "USER" + System.currentTimeMillis();
        User newUser = new User(userId, username, email, initialCash);
        
        users.put(userId, newUser);
        FileManager.saveUser(newUser);
        
        System.out.println("Registration successful! Please login.");
    }
    
    private void viewMarketData() {
        System.out.println("\n=== Market Data ===");
        System.out.printf("%-8s %-20s %-12s %-12s %-12s %-15s%n", 
                         "Symbol", "Company", "Price", "Change", "Change%", "Last Updated");
        System.out.println("-".repeat(85));
        
        for (Stock stock : marketData.getAllStocks().values()) {
            BigDecimal change = stock.getPriceChange();
            BigDecimal changePercent = stock.getPriceChangePercentage();
            String changeSymbol = change.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            
            System.out.printf("%-8s %-20s %-12s %s%-11s %s%-11s %-15s%n",
                            stock.getSymbol(),
                            stock.getName(),
                            currencyFormat.format(stock.getCurrentPrice()),
                            changeSymbol,
                            currencyFormat.format(change),
                            changeSymbol,
                            String.format("%.2f%%", changePercent),
                            stock.getLastUpdated().format(dateFormat));
        }
    }
    
    private void viewPortfolio() {
        Portfolio portfolio = currentUser.getPortfolio();
        System.out.println("\n=== Your Portfolio ===");
        System.out.println("Cash: " + currencyFormat.format(portfolio.getCash()));
        
        Map<String, Integer> holdings = portfolio.getHoldings();
        if (holdings.isEmpty()) {
            System.out.println("No stock holdings.");
            return;
        }
        
        System.out.printf("%-8s %-12s %-12s %-12s %-12s %-12s%n", 
                         "Symbol", "Quantity", "Avg Cost", "Current", "Value", "Gain/Loss");
        System.out.println("-".repeat(72));
        
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalGainLoss = BigDecimal.ZERO;
        
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            Stock stock = marketData.getStock(symbol);
            
            if (stock != null) {
                BigDecimal avgCost = portfolio.getAvgCost(symbol);
                BigDecimal currentPrice = stock.getCurrentPrice();
                BigDecimal value = portfolio.getStockValue(symbol, stock);
                BigDecimal gainLoss = portfolio.getGainLoss(symbol, stock);
                
                totalValue = totalValue.add(value);
                totalGainLoss = totalGainLoss.add(gainLoss);
                
                String gainLossSymbol = gainLoss.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                
                System.out.printf("%-8s %-12d %-12s %-12s %-12s %s%-11s%n",
                                symbol, quantity,
                                currencyFormat.format(avgCost),
                                currencyFormat.format(currentPrice),
                                currencyFormat.format(value),
                                gainLossSymbol,
                                currencyFormat.format(gainLoss));
            }
        }
        
        System.out.println("-".repeat(72));
        System.out.println("Total Portfolio Value: " + 
                          currencyFormat.format(portfolio.getPortfolioValue(marketData.getAllStocks())));
        String totalGainLossSymbol = totalGainLoss.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        System.out.println("Total Gain/Loss: " + totalGainLossSymbol + 
                          currencyFormat.format(totalGainLoss));
    }
    
    private void buyStock() {
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().toUpperCase();
        
        Stock stock = marketData.getStock(symbol);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        
        System.out.println("Stock: " + stock.getName() + " (" + symbol + ")");
        System.out.println("Current Price: " + currencyFormat.format(stock.getCurrentPrice()));
        System.out.println("Available Cash: " + 
                          currencyFormat.format(currentUser.getPortfolio().getCash()));
        
        System.out.print("Enter quantity to buy: ");
        int quantity = getIntInput();
        
        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }
        
        BigDecimal totalCost = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        System.out.println("Total Cost: " + currencyFormat.format(totalCost));
        
        System.out.print("Confirm purchase? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            if (currentUser.getPortfolio().buyStock(stock, quantity)) {
                System.out.println("Purchase successful!");
                FileManager.savePortfolioData(users);
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("Purchase cancelled.");
        }
    }
    
    private void sellStock() {
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().toUpperCase();
        
        Stock stock = marketData.getStock(symbol);
        if (stock == null) {
            System.out.println("Stock not found.");
            return;
        }
        
        Portfolio portfolio = currentUser.getPortfolio();
        int holdings = portfolio.getHoldings().getOrDefault(symbol, 0);
        
        if (holdings == 0) {
            System.out.println("You don't own any shares of " + symbol);
            return;
        }
        
        System.out.println("Stock: " + stock.getName() + " (" + symbol + ")");
        System.out.println("Current Price: " + currencyFormat.format(stock.getCurrentPrice()));
        System.out.println("Holdings: " + holdings + " shares");
        
        System.out.print("Enter quantity to sell: ");
        int quantity = getIntInput();
        
        if (quantity <= 0 || quantity > holdings) {
            System.out.println("Invalid quantity.");
            return;
        }
        
        BigDecimal totalValue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        System.out.println("Total Value: " + currencyFormat.format(totalValue));
        
        System.out.print("Confirm sale? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            if (portfolio.sellStock(stock, quantity)) {
                System.out.println("Sale successful!");
                FileManager.savePortfolioData(users);
            } else {
                System.out.println("Sale failed.");
            }
        } else {
            System.out.println("Sale cancelled.");
        }
    }
    
    private void viewTransactionHistory() {
        System.out.println("\n=== Transaction History ===");
        
        for (Transaction transaction : currentUser.getPortfolio().getTransactions()) {
            System.out.printf("%s | %s | %s %d shares of %s at %s | %s%n",
                            transaction.getTransactionId(),
                            transaction.getTimestamp().format(dateFormat),
                            transaction.getType(),
                            transaction.getQuantity(),
                            transaction.getStockSymbol(),
                            currencyFormat.format(transaction.getPrice()),
                            currencyFormat.format(transaction.getTotalValue()));
        }
    }
    
    private void viewPortfolioPerformance() {
        Portfolio portfolio = currentUser.getPortfolio();
        BigDecimal currentValue = portfolio.getPortfolioValue(marketData.getAllStocks());
        
        System.out.println("\n=== Portfolio Performance ===");
        System.out.println("Current Portfolio Value: " + currencyFormat.format(currentValue));
        System.out.println("Available Cash: " + currencyFormat.format(portfolio.getCash()));
        
        // Calculate total invested
        BigDecimal totalInvested = BigDecimal.ZERO;
        for (Transaction transaction : portfolio.getTransactions()) {
            if (transaction.getType() == Transaction.Type.BUY) {
                totalInvested = totalInvested.add(transaction.getTotalValue());
            } else {
                totalInvested = totalInvested.subtract(transaction.getTotalValue());
            }
        }
        
        System.out.println("Total Invested: " + currencyFormat.format(totalInvested));
        
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal performancePercent = currentValue.subtract(totalInvested)
                                          .divide(totalInvested, 4, BigDecimal.ROUND_HALF_UP)
                                          .multiply(BigDecimal.valueOf(100));
            
            String performanceSymbol = performancePercent.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            System.out.println("Performance: " + performanceSymbol + 
                              String.format("%.2f%%", performancePercent));
        }
    }
    
    private void logout() {
        FileManager.savePortfolioData(users);
        currentUser = null;
        System.out.println("Logged out successfully.");
    }
    
    private void exit() {
        FileManager.savePortfolioData(users);
        marketData.stopPriceUpdates();
        System.out.println("Thank you for using Stock Trading Platform!");
        System.exit(0);
    }
    
    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    
    private BigDecimal getBigDecimalInput() {
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid amount: ");
            }
        }
    }
    
    public static void main(String[] args) {
        TradingPlatform platform = new TradingPlatform();
        platform.start();
    }
}
