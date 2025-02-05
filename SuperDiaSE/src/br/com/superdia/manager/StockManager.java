package br.com.superdia.manager;

import java.util.List;
import java.util.Scanner;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import br.com.superdia.modelo.Produto;

public class StockManager {
    private static final String BASE_URL = "http://localhost:8080/SuperDiaWebApi/rest/";
    private static String email, password;

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
                    case 4 -> listProduct();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.err.println("Opção inválida!");
                }
            }
        } while (option != 0);
        scanner.close();
    }

    private static void addProduct(Scanner scanner) {
        System.out.println("\n=== ADICIONAR PRODUTO ===");
        String nome = readString("Nome do Produto: ", scanner);
        String descricao = readString("Descrição: ", scanner);
        double preco = readDouble("Preço: ", scanner);
        int estoqueMinimo = readInt("Estoque Mínimo: ", scanner);
        int quantidadeEstoque = readInt("Quantidade em Estoque: ", scanner);

        Produto produto = new Produto(nome, descricao, preco, estoqueMinimo, quantidadeEstoque);
        sendPostRequest("produto/create", produto, "Produto cadastrado com sucesso!");
    }

    private static void updateProduct(Scanner scanner) {
        System.out.println("\n=== ATUALIZAR PRODUTO ===");
        int id = readInt("ID do Produto a atualizar: ", scanner);
        String nome = readString("Novo Nome: ", scanner);
        String descricao = readString("Nova Descrição: ", scanner);
        double preco = readDouble("Novo Preço: ", scanner);
        int estoqueMinimo = readInt("Novo Estoque Mínimo: ", scanner);
        int quantidadeEstoque = readInt("Nova Quantidade em Estoque: ", scanner);

        Produto produto = new Produto(id, nome, descricao, preco, estoqueMinimo, quantidadeEstoque);
        sendPutRequest("produto/update", produto, "Produto atualizado com sucesso!");
    }

    private static void removeProduct(Scanner scanner) {
        System.out.println("\n=== REMOVER PRODUTO ===");
        int id = readInt("ID do Produto a remover: ", scanner);

        Produto produto = new Produto(id);
        sendPostRequest("produto/delete", produto, "Produto removido com sucesso!");
    }

    private static void listProduct() {
        System.out.println("\n=== LISTA DE PRODUTOS ===");

        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URL + "produto/getProdutos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(authJson()));

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            List<Produto> produtos = response.readEntity(new GenericType<>() {});
            produtos.forEach(System.out::println);
        } else {
            System.err.println("Erro ao buscar produtos: " + response.readEntity(String.class));
        }

        response.close();
        client.close();
    }

    private static boolean obtainCredentials(Scanner scanner) {
        email = readString("Digite seu email de usuário: ", scanner);
        password = readString("Digite sua senha: ", scanner);
        return authenticateUser(email, password);
    }

    private static void displayOptions() {
        System.out.println("\n=== GERENCIAMENTO DE ESTOQUE ===");
        System.out.println("1. Cadastrar Produto");
        System.out.println("2. Atualizar Produto");
        System.out.println("3. Remover Produto");
        System.out.println("4. Listar Produtos");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static boolean authenticateUser(String email, String password) {
        String json = authJson();
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URL + "usuario/authenticate")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(json));

        String responseBody = response.readEntity(String.class);
        response.close();
        client.close();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Autenticação bem-sucedida.");
            return true;
        } else {
            System.err.println("Erro: " + responseBody);
            return false;
        }
    }

    private static void sendPostRequest(String endpoint, Produto produto, String successMessage) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URL + endpoint)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(authJson(produto)));

        handleResponse(response, successMessage);
        client.close();
    }

    private static void sendPutRequest(String endpoint, Produto produto, String successMessage) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URL + endpoint)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(authJson(produto)));

        handleResponse(response, successMessage);
        client.close();
    }

    private static void handleResponse(Response response, String successMessage) {
        if (response.getStatus() == Response.Status.OK.getStatusCode() || 
            response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            System.out.println(successMessage);
        } else {
            System.err.println("Erro: " + response.readEntity(String.class));
        }
        response.close();
    }

    private static String authJson() {
        return "{\"login\":\"" + email + "\",\"senha\":\"" + password + "\"}";
    }

    private static String authJson(Produto produto) {
        String json = "{\"login\":\"" + email + "\",\"senha\":\"" + password + "\",\"produto\":" + produto.toJson() + "}";
        System.out.println("JSON Enviado: " + json);
        return json;
    }


    private static String readString(String message, Scanner scanner) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private static double readDouble(String message, Scanner scanner) {
        System.out.print(message);
        while (!scanner.hasNextDouble()) {
            System.err.print("Entrada inválida! Digite um número válido: ");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }

    private static int readInt(String message, Scanner scanner) {
        System.out.print(message);
        while (!scanner.hasNextInt()) {
            System.err.print("Entrada inválida! Digite um número válido: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }
}