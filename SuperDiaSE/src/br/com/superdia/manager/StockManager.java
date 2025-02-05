package br.com.superdia.manager;

import java.util.Scanner;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class StockManager {
    private static final String BASE_URL = "http://localhost:8080/SuperDiaWebApi/rest/";

    public static void main(String[] args) {
        stockManager();
    }

    private static void stockManager() {
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        boolean authorized = false;

        do {
            if (!authorized)
                authorized = obtainCredentials(scanner);
            else {
                displayOptions();
                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1 -> addProduct(scanner);
                    case 2 -> updateProduct(scanner);
                    case 3 -> removeProduct(scanner);
                    case 4 -> listProduct(scanner);
                    case 0 -> System.out.println("Saindo...");
                    default -> System.err.println("Opção inválida!");
                }
            }
        } while (option != 0);
        scanner.close();
    }

    private static void listProduct(Scanner scanner) {
        // Your list product logic here
    }

    private static void removeProduct(Scanner scanner) {
        // Your remove product logic here
    }

    private static void updateProduct(Scanner scanner) {
        // Your update product logic here
    }

    private static void addProduct(Scanner scanner) {
        // Your add product logic here
    }

    private static boolean obtainCredentials(Scanner scanner) {
        String email, password;

        email = readString("Digite seu email de usuário: ", scanner);
        password = readString("Digite sua senha: ", scanner);

        // Authenticate the user by sending a request to the backend
        return authenticateUser(email, password);
    }

    private static String readString(String message, Scanner scanner) {
        System.out.println(message);
        return scanner.nextLine();
    }

    private static void displayOptions() {
        // Display available options for the user
    }

    // Authentication method
    private static boolean authenticateUser(String email, String password) {
        // Create a JSON object with email and password
        String json = "{\"login\":\"" + email + "\",\"senha\":\"" + password + "\"}";

        // Send the request to the authenticate endpoint
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URL + "usuario/authenticate")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(json));

        // Check if the authentication was successful
        String responseBody = response.readEntity(String.class);
        response.close();
        client.close();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            // If response is OK, assume authentication is successful and return true
            System.out.println("Autenticação bem-sucedida.");
            return true;
        } else {
            // If response is not OK, print the error message
            System.err.println("Erro: " + responseBody);
            return false;
        }
    }
}
