package br.com.superdia.modelo;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private double preco;
    private int estoqueMinimo;
    private int quantidadeEstoque;
    private String urlImagem;
    private String vendidoPor;

	public Produto() {
    }

    public Produto(String nome, String descricao, double preco, int estoqueMinimo, int quantidadeEstoque) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoqueMinimo = estoqueMinimo;
        this.quantidadeEstoque = quantidadeEstoque;
    }
    
    public Produto(String nome, String descricao, double preco, int estoqueMinimo, int quantidadeEstoque, String vendidoPor, String urlImagem) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoqueMinimo = estoqueMinimo;
        this.quantidadeEstoque = quantidadeEstoque;
        this.vendidoPor = vendidoPor;
        this.urlImagem = urlImagem;
    }

    public Produto(Long id, String nome, String descricao, double preco, int estoqueMinimo, int quantidadeEstoque, String vendidoPor, String urlImagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoqueMinimo = estoqueMinimo;
        this.quantidadeEstoque = quantidadeEstoque;
    }
    
    public Produto(Long id, String nome, String descricao, double preco, int estoqueMinimo, int quantidadeEstoque,
			String urlImagem) {
		super();
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.preco = preco;
		this.estoqueMinimo = estoqueMinimo;
		this.quantidadeEstoque = quantidadeEstoque;
		this.urlImagem = urlImagem;
	}
    
	public Produto(String nome, String descricao, double preco, int estoqueMinimo, int quantidadeEstoque,
			String urlImagem) {
		super();
		this.nome = nome;
		this.descricao = descricao;
		this.preco = preco;
		this.estoqueMinimo = estoqueMinimo;
		this.quantidadeEstoque = quantidadeEstoque;
		this.urlImagem = urlImagem;
	}

	public Produto(Long id) {
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
    
    public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

	public String toJson() {
	    String precoFormatted = String.format("%.2f", preco).replace(',', '.');
	    return String.format(
	        "{\"id\":%d,\"nome\":\"%s\",\"descricao\":\"%s\",\"preco\":%s,\"estoqueMinimo\":%d,\"quantidadeEstoque\":%d,\"vendidoPor\":\"%s\",\"urlImagem\":\"%s\"}",
	        id, nome, descricao, precoFormatted, estoqueMinimo, quantidadeEstoque, vendidoPor, urlImagem
	    );
	}


    @Override
    public String toString() {
        return String.format(
            "ID: %6d \t %-55s \t R$ %8.2f \t %d un.",
            id, formatarNome(nome), preco, quantidadeEstoque
        );
    }

    public static String formatarNome(String nome) {
        if (nome.length() > 55) {
            return nome.substring(0, 52) + "...";
        }
        return String.format("%-55s", nome);
    }

	public String getVendidoPor() {
		return vendidoPor;
	}

	public void setVendidoPor(String vendidoPor) {
		this.vendidoPor = vendidoPor;
	}
}
