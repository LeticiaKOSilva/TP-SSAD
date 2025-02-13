package br.com.superdia.modelo;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Produto produto;
    
    private Integer quantidade;
    private Double valorUnitario;

    public Item() {}

    public Item(Long id, Produto produto, Integer quantidade, Double valorUnitario) {
		super();
		this.id = id;
		this.produto = produto;
		this.quantidade = quantidade;
		this.valorUnitario = valorUnitario;
	}
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public String toJson() {
        String valorUnitarioFormatted = String.format("%.2f", valorUnitario).replace(',', '.');
        return String.format(
            "{\"id\":%d,\"produto\":%s,\"quantidade\":%d,\"valorUnitario\":%s}",
            id, produto != null ? produto.toJson() : "null", quantidade, valorUnitarioFormatted
        );
    }

	@Override
	public String toString() {
		return String.format("Item [id=%s, produto=%s, quantidade=%s, valorUnitario=%s]", id, produto, quantidade,
				valorUnitario);
	}
	
	public Double getValorTotal() {
		return quantidade * valorUnitario;
	}

}
