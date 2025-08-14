import java.io.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

class FileManager {
    private static final String USERS_FILE = "users.dat";
    private static final String PORTFOLIOS_FILE = "portfolios.dat";
    
    public static void saveUser(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(USERS_FILE, true))) {
            oos.writeObject(user);
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }
    
    public static Map<String, User> loadUsers() {
        Map<String, User> users = new HashMap<>();
        File file = new File(USERS_FILE);
        
        if (!file.exists()) {
            return users;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(USERS_FILE))) {
            while (true) {
                try {
                    User user = (User) ois.readObject();
                    users.put(user.getUserId(), user);
                } catch (EOFException e) {
                    break; // End of file reached
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        
        return users;
    }
    
    public static void savePortfolioData(Map<String, User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(PORTFOLIOS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving portfolio data: " + e.getMessage());
        }
    }
}

