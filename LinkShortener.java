import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LinkShortener {
    private static final int SHORT_URL_LENGTH = 6;
    private static final String LINKS_FILENAME = "links.txt";

    private Map<String, String> shortToLongMap;
    private Map<String, String> longToShortMap;

    public LinkShortener() {
        shortToLongMap = new HashMap<>();
        longToShortMap = new HashMap<>();

        // Load existing link mappings from file, if any
        loadLinksFromFile();
    }

    public String shortenUrl(String longUrl) {
        if (longToShortMap.containsKey(longUrl)) {
            return longToShortMap.get(longUrl);
        }

        String shortUrl = generateShortUrl();
        shortToLongMap.put(shortUrl, longUrl);
        longToShortMap.put(longUrl, shortUrl);

        // Save the updated link mappings to file
        saveLinksToFile();

        return shortUrl;
    }

    public String expandUrl(String shortUrl) {
        return shortToLongMap.getOrDefault(shortUrl, "Invalid short URL");
    }

    private String generateShortUrl() {
        // Implement a basic hash function to generate a short URL
        // You can use a proper hashing algorithm for production use
        String hash = hashString(Integer.toString(shortToLongMap.size()));
        return hash.substring(0, SHORT_URL_LENGTH);
    }

    private String hashString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing the input.", e);
        }
    }

    private void loadLinksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LINKS_FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    shortToLongMap.put(parts[0], parts[1]);
                    longToShortMap.put(parts[1], parts[0]);
                }
            }
        } catch (IOException e) {
            // Handle file read error
            System.err.println("Error loading links from file: " + e.getMessage());
        }
    }

    private void saveLinksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LINKS_FILENAME))) {
            for (Map.Entry<String, String> entry : shortToLongMap.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            // Handle file write error
            System.err.println("Error saving links to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        LinkShortener linkShortener = new LinkShortener();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Shorten URL");
            System.out.println("2. Expand URL");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter the long URL to shorten: ");
                    String longUrl = scanner.nextLine();
                    String shortUrl = linkShortener.shortenUrl(longUrl);
                    System.out.println("Shortened URL: " + shortUrl);
                    break;

                case 2:
                    System.out.print("Enter the short URL to expand: ");
                    String shortUrlToExpand = scanner.nextLine();
                    String expandedUrl = linkShortener.expandUrl(shortUrlToExpand);
                    System.out.println("Expanded URL: " + expandedUrl);
                    break;

                case 3:
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please choose a valid option.");
            }
        }
    }
}
