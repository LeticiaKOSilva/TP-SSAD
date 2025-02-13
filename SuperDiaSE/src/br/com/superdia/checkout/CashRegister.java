package br.com.superdia.checkout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.superdia.modelo.Item;
import br.com.superdia.modelo.NotaFiscal;
import br.com.superdia.modelo.Pessoa;
import br.com.superdia.modelo.Produto;
import br.com.superdia.modelo.Usuario;
import br.com.superdia.soap.CreditCardValidatorService;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class CashRegister {
	
	private static final String BASE_URL = "http://localhost:8080/SuperDiaWebApi/rest/";
	
	private static final int AUTH_SUCCESS = 1,
			AUTH_FAILURE = 0,
			AUTH_ERROR = -1,
			PAYMENT_PIX = 2,
			PAYMENT_CARD = 1,
			PAYMENT_CASH = 0;
	
	private Usuario cashier;
	private List<Item> cart;
	
	public static void main(String[] args) {
		new CashRegister();
	}
	
	public CashRegister() {
		Scanner scanner = new Scanner(System.in);
		
		cashier = new Usuario();
		clearCart();
		
        if(authenticate(scanner))
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
		cart = new ArrayList<Item>();
	}

	private void cartMenu(Scanner scanner) {
		if(cart == null) {
			System.out.println("\033[33m\n\nNão foi encontrada nenhuma compra em aberto! Utilize a opção 1.\n\033[0m");
			return;
		}
		
		int option = -1;
        do {
        	displayCartMenuOptions();
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
            	case 1 -> addProduct(scanner);
            	case 2 -> removeProduct(scanner);
            	case 3 -> listCartItens();            	
            	case 4 -> listProducts();            	
            	case 5 -> { checkout(scanner); return; } 
            	case 6 -> { clearCart(); System.out.println("\033[33m\n\nCompra cancelada!\n\033[0m"); return; }
                case 0 -> { return; } 
                default -> System.err.println("\n\nOpção inválida!\n");
            }
        } while (option != 0);
	}

	private void addProduct(Scanner scanner) {
	    System.out.println("\n=== ADICIONAR PRODUTO ===");
	    
	    Produto product = fetchProduct(scanner);
	    if (product == null) { System.out.println("\033[33m\n\nProduto não encontrado!\n\033[0m"); return; }
	    
	    int quantity = requestQuantity(scanner, product);
	    if (quantity == 0) return;
	    
	    addOrUpdateCartItem(cart, product, quantity);
	}
	
	private Produto fetchProduct(Scanner scanner) {
		Produto product = new Produto();
	    product.setId(Long.valueOf(readInt("ID do Produto: ", scanner)));
	    
	    Client client = ClientBuilder.newClient();
	    Response response = client.target(BASE_URL + "produto/getById")
	            .request(MediaType.APPLICATION_JSON)
	            .post(Entity.json(authJson(product)));
	    
	    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
	        product = response.readEntity(Produto.class);
	    } else {
	        System.err.println("Erro: " + response.readEntity(String.class));
	        product = null;
	    }
	    
	    response.close();
	    client.close();
	    return product;
	}
	
	private int requestQuantity(Scanner scanner, Produto product) {
	    int quantity;
	    do {
	        quantity = readInt(String.format("Quantidade (%d em estoque) [0 - Cancelar]: ", product.getQuantidadeEstoque()), scanner);
	        if (quantity == 0) return 0;
	    } while (quantity < 0 || quantity > product.getQuantidadeEstoque());
	    
	    return quantity;
	}
	
	private void addOrUpdateCartItem(List<Item> cart, Produto product, int quantity) {
	    Optional<Item> existingItem = cart.stream()
	        .filter(i -> i.getProduto().getId().equals(product.getId()))
	        .findFirst();
	    
	    if (existingItem.isPresent()) {
	        Item item = existingItem.get();
	        int newQuantity = item.getQuantidade() + quantity;
	        
	        if (newQuantity > product.getQuantidadeEstoque()) {
	            System.out.println("\033[31mQuantidade em estoque é insuficiente!\033[0m");
	        } else {
	            item.setQuantidade(newQuantity);
	            System.out.println("\033[33m\n\nQuantidade atualizada!\n\033[0m");
	        }
	    } else {
	        Item item = new Item();
	        item.setProduto(product);
	        item.setValorUnitario(product.getPreco());
	        item.setQuantidade(quantity);
	        cart.add(item);
	        
	        System.out.println("\033[32m\n\nItem adicionado ao carrinho!\n\033[0m");
	    }
	}

	private void removeProduct(Scanner scanner) {
		if(cart.size() == 0) { System.out.println("\033[33m\n\nCarinho vazio!\n\033[0m"); return; }
		
	    System.out.println("\n=== REMOVER PRODUTO ===");
	    
	    Produto product = fetchProduct(scanner);
	    if (product == null) return;

	    Optional<Item> existingItem = cart.stream()
	        .filter(i -> i.getProduto().getId().equals(product.getId()))
	        .findFirst();
	    
	    if (existingItem.isPresent()) {
	        Item item = existingItem.get();
	        int availableQuantity = item.getQuantidade();
	        
	        int quantityToRemove = readInt(String.format("Quantidade a remover (máximo %d disponível no carrinho): ", availableQuantity), scanner);
	        
	        if (quantityToRemove <= 0) {
	            System.out.println("\033[31mErro: A quantidade deve ser maior que zero.\033[0m");
	            return;
	        }
	        
	        if (quantityToRemove > availableQuantity) {
	            System.out.println(String.format("\033[31m\n\nNão é possível remover mais do que a quantidade disponível (%d).\033[0m", availableQuantity));
	        } else if (quantityToRemove == availableQuantity) {
	            cart.remove(item);
	            System.out.println("\033[32mProduto removido do carrinho!\033[0m");
	        } else {
	            item.setQuantidade(availableQuantity - quantityToRemove);
	            System.out.println(String.format("\033[33m\n\nQuantidade do produto no carrinho atualizada para %d!\033[0m", item.getQuantidade()));
	        }
	    } else {
	        System.out.println("\033[33m\n\nProduto não encontrado no carrinho.\n\033[0m");
	    }
	}

	
	private void listCartItens() {
		if(cart.size() == 0) { System.out.println("\033[33m\n\nCarinho vazio!\n\033[0m"); return; }
		
		System.out.println("\n=== ITENS DO CARRINHO ===");
		cart.stream()
	    .map(item -> String.format("ID: %6d    %-20s Valor unitário: R$ %8.2f \t Quantidade: %d un.",
	    						item.getProduto().getId(), Produto.formatarNome(item.getProduto().getNome()), 
	                            item.getValorUnitario(), item.getQuantidade()))
	    .forEach(System.out::println);
		
		System.out.println("\nTotal: R$ " + String.format("%.2f", calculateTotalValue(cart)));
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

	private void checkout(Scanner scanner) {
		if(cart.size() == 0) { System.out.println("\033[33m\n\nCarinho vazio!\n\033[0m"); return; }
		
	    listCartItens();

	    double totalValue = calculateTotalValue(cart);
	    
	    Usuario cliente;
	    while((cliente = fetchClient(scanner)) == null);

	    int paymentMethod = requestPaymentMethod(scanner);
	    processPayment(scanner, totalValue, paymentMethod);

	    finalizePurchase(cliente, totalValue);
	    
	    clearCart();
	}

	private double calculateTotalValue(List<Item> cart) {
	    return cart.stream()
	        .mapToDouble(item -> item.getValorTotal())
	        .sum();
	}
	
	private Usuario fetchClient(Scanner scanner) {
		Usuario usuario = new Usuario();
		
		usuario.setPessoa(new Pessoa("", requestEmail(scanner)));
	    
	    Client client = ClientBuilder.newClient();
	    Response response = client.target(BASE_URL + "usuario/getByEmail")
	            .request(MediaType.APPLICATION_JSON)
	            .post(Entity.json(authJson(usuario)));
	    
	    usuario = (response.getStatus() == Response.Status.OK.getStatusCode()) ? response.readEntity(Usuario.class) : null;
	    if(usuario == null) System.out.print("\nCliente não encontrado!");
	    
	    response.close();
	    client.close();
	    return usuario;
	}

	private String requestEmail(Scanner scanner) {
	    String email;
	    
	    do {
	        email = readString("\nE-mail do cliente: ", scanner);
	    } while (!email.matches("^[\\w-]+(?:\\.[\\w-]+)*@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,7}$"));
	    return email;
	}

	private int requestPaymentMethod(Scanner scanner) {
	    do {
	        int choice = readInt(String.format("\nEscolha a forma de pagamento:\n1. Cartão\n2. Pix\n3. Dinheiro\nOpção: "), scanner);
	        switch (choice) {
	            case 1: return PAYMENT_CARD;
	            case 2: return PAYMENT_PIX;
	            case 3: return PAYMENT_CASH;
	        }
	        System.out.println("\nOpção inválida! Tente novamente.");
	    } while (true);
	}
	
	private void processPayment(Scanner scanner, double totalValue, int paymentMethod) {
	    switch (paymentMethod) {
	        case PAYMENT_PIX:
	            System.out.println("\033[32m\nPagamento via PIX confirmado!\n\033[0m");
	            break;
	        case PAYMENT_CARD:
	        	processCardPayment(scanner);
	            break;
	        case PAYMENT_CASH:
	        	processCashPayment(scanner, totalValue);
	            break;
	    }
	}
	
	private void processCashPayment(Scanner scanner, double totalValue) {
	    double amountGiven;
	    
	    do {
	        amountGiven = readDouble(String.format("\nValor total: R$ %.2f\nInforme o valor recebido: R$ ", totalValue), scanner);
	        if (amountGiven < totalValue) {
	            System.out.println("\033[31mValor insuficiente! Tente novamente.\033[0m");
	        }
	    } while (amountGiven < totalValue);

	    double change = amountGiven - totalValue;
	    System.out.printf("\033[32mTroco: R$ %.2f\n\033[0m", change);
	}

	private void processCardPayment(Scanner scanner) {
		boolean isValid;
	    do {
	        String cardNumber = readString("\nNúmero do Cartão: ", scanner);
	        String expiryDate = readString("Data de Validade (MM/YY): ", scanner);

	        cardNumber = cardNumber.replaceAll("\\s+", "").trim();
	        expiryDate = expiryDate.replace("/", "").trim();

	        CreditCardValidatorService validator = new CreditCardValidatorService();
	        isValid = validator.validateCard(cardNumber, expiryDate);

	        if (!isValid) {
	            System.err.println("Cartão de crédito inválido. Tente novamente.");
	        }
	    } while (!isValid);
	    
	    System.out.println("\033[32mPagamento via cartão confirmado!\n\033[0m");
	}

	private void finalizePurchase(Usuario client, double totalValue) {
	    NotaFiscal notaFiscal = new NotaFiscal();
	    notaFiscal.setCliente(client);
	    notaFiscal.setData(Calendar.getInstance());

	    for (Item item : cart) {
	        Produto produto = item.getProduto();
	        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
	        updateProduto(produto); 
	    }
	    
	    notaFiscal.setItens(cart);
	    persistNotaFiscal(notaFiscal);
		
	    System.out.println("\n\n\n=== NOTA FISCAL ===");
	    System.out.println("Data: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(notaFiscal.getData().getTime()));
	    System.out.println("Cliente: " + client.getPessoa().getEmail() + "\n");
	    cart.stream()
	    .map(item -> String.format("ID: %6d    %-20s Valor unitário: R$ %8.2f \t Quantidade: %d un.",
	    						item.getProduto().getId(), Produto.formatarNome(item.getProduto().getNome()), 
	                            item.getValorUnitario(), item.getQuantidade()))
	    .forEach(System.out::println);
	    System.out.println("\nTotal: R$ " + String.format("%.2f", totalValue));
	    System.out.println("\u001B[32m\n\nCompra finalizada com sucesso!\u001B[0m");
	}
	
	private void updateProduto(Produto produto) {
	    try (Client client = ClientBuilder.newClient()) {
	        Response response = client.target(BASE_URL + "produto/update")
	                .request(MediaType.APPLICATION_JSON)
	                .put(Entity.json(authJson(produto)));

	        if (response.getStatus() == Response.Status.OK.getStatusCode() || 
	            response.getStatus() == Response.Status.CREATED.getStatusCode()) {
	        } else {
	            System.err.println("Erro P: " + response.readEntity(String.class));
	        }
	        response.close();
	    }
	}
	
	private void persistNotaFiscal(NotaFiscal notaFiscal) {
	    try (Client client = ClientBuilder.newClient()) {
	        Response response = client.target(BASE_URL + "notafiscal/create")
	                .request(MediaType.APPLICATION_JSON)
	                .post(Entity.json(authJson(notaFiscal)));

	        if (response.getStatus() == Response.Status.OK.getStatusCode() || 
	            response.getStatus() == Response.Status.CREATED.getStatusCode()) {
	        } else {
	            System.err.println("Erro NF: " + response.readEntity(String.class));
	        }
	        response.close();
	    }
	}

	
	private void clearCart() {
		cart = null;
	}

	private boolean authenticate(Scanner scanner) {
        int authorized;
        do {
        	authorized = obtainCredentials(scanner);
        	if (authorized == AUTH_ERROR) return false;
        } while(authorized == AUTH_FAILURE);

        displayWelcomeMessage(cashier.getPessoa().getNome());
        return true;
	}

	private int obtainCredentials(Scanner scanner) {
        String email = readString("Digite seu email de usuário: ", scanner);
        String password = readString("Digite sua senha: ", scanner);
        return authenticateUser(email, password);
    }
	
    private int authenticateUser(String email, String password) {
        String json = authJson(email, password);
        Client client = ClientBuilder.newClient();
        Response response;
        
        try {
        	response = client.target(BASE_URL + "usuario/authenticate")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(json));
        } catch (Exception e) {
        	System.err.println("\nNão foi possível se conectar ao servidor! Finalizando...");
        	return AUTH_ERROR;
		}

        String responseBody = response.readEntity(String.class);
        response.close();
        client.close();
        

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Usuario usuario = objectMapper.readValue(responseBody, Usuario.class);
                if (!usuario.getPerfil().equals(Usuario.PERFIL_CAIXA)) {
                	System.err.println("\nPerfil inválido! Tente novamente.");
                	return AUTH_FAILURE;
                }
                cashier = usuario;
            } catch (Exception e) {
                System.err.println("\nErro ao desserializar JSON: " + e.getMessage());
            }
            return AUTH_SUCCESS;
        } else {
            System.err.println("\nCredenciais incorretas! Tente novamente.");
            return AUTH_FAILURE;
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
    		    "\n=== COMPRA ===\n" +
    		    "1. Adicionar Produto\n" +
    		    "2. Remover Produto\n" +
    		    "3. Ver carrinho\n" +
    		    "4. Listar Produtos\n" +
    		    "5. Finalizar Compra\n" +
    		    "6. Cancelar Compra\n" +
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
        return "{\"login\":\"" + cashier.getPessoa().getEmail() + "\",\"senha\":\"" + cashier.getSenha() + "\"}";
    }
    
    private String authJson(String email, String password) {
        return "{\"login\":\"" + email + "\",\"senha\":\"" + password + "\"}";
    }

    private String authJson(Produto produto) {
        return "{\"login\":\"" + cashier.getPessoa().getEmail() + "\",\"senha\":\"" + cashier.getSenha() + "\",\"produto\":" + produto.toJson() + "}";
    }
    
    private String authJson(Usuario usuario) {
        return "{\"login\":\"" + cashier.getPessoa().getEmail() + "\",\"senha\":\"" + cashier.getSenha() + "\",\"usuario\":" + usuario.toJson() + "}";
    }
    
    private String authJson(NotaFiscal notaFiscal) {
        return "{\"login\":\"" + cashier.getPessoa().getEmail() + "\",\"senha\":\"" + cashier.getSenha() + "\",\"notaFiscal\":" + notaFiscal.toJson() + "}";
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
