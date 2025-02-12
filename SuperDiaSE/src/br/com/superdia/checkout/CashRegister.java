package br.com.superdia.checkout;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.superdia.modelo.Produto;
import br.com.superdia.modelo.Usuario;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class CashRegister {
	
	private static final String BASE_URL = "http://localhost:8080/SuperDiaWebApi/rest/";
	
	private String email, password;
	
	private Usuario cashier;
	private List<Produto> cart;
	
	public static void main(String[] args) {
		new CashRegister();
	}
	
	public CashRegister() {
		Scanner scanner = new Scanner(System.in);
		
		cashier = new Usuario();
		cart = null;
		
        authenticate(scanner);
        menu(scanner);
        
        scanner.close();
	}

	private void menu(Scanner scanner) {
		int option = -1;
        do {
        	displayMainMenuOptions();
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
            	case 1 -> { newCart(); cartMenu(scanner); }
            	case 2 -> cartMenu(scanner);
                case 0 -> System.out.println("\n\nFinalizando o sistema...\n");
                default -> System.err.println("\n\nOpção inválida!\n");
            }
        } while (option != 0);
	}

	private void newCart() {
		cart = new ArrayList<Produto>();
	}

	private void cartMenu(Scanner scanner) {
		if(cart == null) {
			System.out.println("\nNão foi encontrado nenhum carrinho! Utilize a opção 1.\n");
			return;
		}
		
		int option = -1;
        do {
        	displayCartMenuOptions();
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
            	case 1 -> addProduct();
            	case 2 -> removeProduct();
            	case 3 -> listProducts();            	
            	case 4 -> checkout();            	
                case 0 -> { return; } 
                default -> System.err.println("\n\nOpção inválida!\n");
            }
        } while (option != 0);
	}
	
	private void addProduct() {
		// TODO Auto-generated method stub
	}

	private void removeProduct() {
		// TODO Auto-generated method stub
	}

	private void listProducts() {
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

	private void checkout() {
		// TODO Auto-generated method stub
	}

	private void authenticate(Scanner scanner) {
        boolean authorized = false;
        do {
        	authorized = obtainCredentials(scanner);
        } while(!authorized);

        displayWelcomeMessage(cashier.getPessoa().getNome());
	}

	private boolean obtainCredentials(Scanner scanner) {
        email = readString("Digite seu email de usuário: ", scanner);
        password = readString("Digite sua senha: ", scanner);
        return authenticateUser(email, password);
    }
	
    private boolean authenticateUser(String email, String password) {
        String json = authJson();
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URL + "usuario/authenticate")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(json));

        String responseBody = response.readEntity(String.class);
        response.close();
        client.close();
        

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Usuario usuario = objectMapper.readValue(responseBody, Usuario.class);
                if (!usuario.getPerfil().equals(Usuario.PERFIL_CAIXA)) {
                	System.err.println("\nPerfil inválido! Tente novamente.");
                	return false;
                }
                cashier = usuario;
            } catch (Exception e) {
                System.err.println("\nErro ao desserializar JSON: " + e.getMessage());
            }
            return true;
        } else {
            System.err.println("\nErro: " + responseBody);
            return false;
        }
    }
	
    private static void displayMainMenuOptions() {
    	System.out.print(String.format(
    		    "\n=== CAIXA REGISTRADORA ===\n" +
    		    "1. Nova Compra\n" +
    		    "2. Carregar Compra\n" +
    		    "0. Sair\n" +
    		    "Escolha uma opção: "
    		));
    }
    
    private static void displayCartMenuOptions() {
    	System.out.print(String.format(
    		    "\n=== CARRINHO ===\n" +
    		    "1. Adicionar Produto\n" +
    		    "2. Remover Produto\n" +
    		    "3. Listar Produtos\n" +
    		    "4. Finalizar Compra\n" +
    		    "0. Voltar\n" +
    		    "Escolha uma opção: "
    		));
    }
    
    private void displayWelcomeMessage(String name) {
    	System.out.print(String.format(
    		    "\033[32m\n\n -- Bem-vindo %s -- \n\033[0m",
    		    name
    		));
	}
    
    private String authJson() {
        return "{\"login\":\"" + email + "\",\"senha\":\"" + password + "\"}";
    }

    private String authJson(Produto produto) {
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
