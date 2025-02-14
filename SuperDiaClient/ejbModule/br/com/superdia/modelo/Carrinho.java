package br.com.superdia.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Carrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Usuario cliente;
    
    @OneToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "carrinho_id")
    private List<Item> itens = new ArrayList<Item>();
    
    public Carrinho() {}

	public Carrinho(Long id, Usuario cliente, List<Item> itens, Calendar data) {
		super();
		this.id = id;
		this.cliente = cliente;
		this.itens = itens;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getCliente() {
		return cliente;
	}

	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}

	public List<Item> getItens() {
		return itens;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
	}
	
	public void removeItem(Item item) {
        itens.removeIf(i -> i.getId().equals(item.getId()));
    }

	public String toJson() {
	    return String.format(
	        "{\"id\":%d,\"cliente\":%s,\"itens\":[%s],\"data\":%d}",
	        id,
	        cliente != null ? cliente.toJson() : "null",
	        itens.stream().map(Item::toJson).collect(Collectors.joining(","))
	    );
	}

}
