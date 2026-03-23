import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CurrencyConverter {

    public static double getLiveRate(String from, String to) throws Exception {
        String urlStr = "https://api.frankfurter.app/latest?base=" + from + "&symbols=" + to;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        // JSON looks like: {"rates":{"CHF":0.8923}}
        // Find the rate value after the currency key
        String json = response.toString();
        String key = "\"" + to + "\":";
        int idx = json.indexOf(key);
        int start = idx + key.length();

        // End is either a comma (more fields follow) or closing brace
        int endComma = json.indexOf(",", start);
        int endBrace = json.indexOf("}", start);
        int end = (endComma != -1 && endComma < endBrace) ? endComma : endBrace;

        return Double.parseDouble(json.substring(start, end).trim());
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println(" ");
        System.out.println("   BANK CURRENCY CONVERTER   ");
        System.out.println("   Rates: frankfurter.dev    ");
        System.out.println(" ");
        System.out.println("1.  USD to EUR");
        System.out.println("2.  EUR to USD");
        System.out.println("3.  USD to GBP");
        System.out.println("4.  GBP to USD");
        System.out.println("5.  EUR to GBP");
        System.out.println("6.  GBP to EUR");
        System.out.println("7.  USD to CHF");
        System.out.println("8.  CHF to USD");
        System.out.println("9.  EUR to CHF");
        System.out.println("10. CHF to EUR");
        System.out.println("11. GBP to CHF");
        System.out.println("12. CHF to GBP");
        System.out.println("==============================");
        System.out.print("Enter your choice (1-12): ");

        int choice = scanner.nextInt();

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();

        try {

            if (choice == 1) {
                double rate = getLiveRate("USD", "EUR");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f USD = %.2f EUR  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 2) {
                double rate = getLiveRate("EUR", "USD");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f EUR = %.2f USD  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 3) {
                double rate = getLiveRate("USD", "GBP");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f USD = %.2f GBP  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 4) {
                double rate = getLiveRate("GBP", "USD");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f GBP = %.2f USD  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 5) {
                double rate = getLiveRate("EUR", "GBP");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f EUR = %.2f GBP  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 6) {
                double rate = getLiveRate("GBP", "EUR");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f GBP = %.2f EUR  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 7) {
                double rate = getLiveRate("USD", "CHF");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f USD = %.2f CHF  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 8) {
                double rate = getLiveRate("CHF", "USD");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f CHF = %.2f USD  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 9) {
                double rate = getLiveRate("EUR", "CHF");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f EUR = %.2f CHF  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 10) {
                double rate = getLiveRate("CHF", "EUR");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f CHF = %.2f EUR  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 11) {
                double rate = getLiveRate("GBP", "CHF");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f GBP = %.2f CHF  (rate: %.4f)%n", amount, result, rate);

            } else if (choice == 12) {
                double rate = getLiveRate("CHF", "GBP");
                double result = Math.round(amount * rate * 100.0) / 100.0;
                System.out.printf("%.2f CHF = %.2f GBP  (rate: %.4f)%n", amount, result, rate);

            } else {
                System.out.println("Invalid choice. Please enter a number between 1 and 12.");
            }

        } catch (Exception e) {
            System.out.println("Error fetching live rate: " + e.getMessage());
            System.out.println("Check your internet connection and try again.");
        }

        scanner.close();
    }
}