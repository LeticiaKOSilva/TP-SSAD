package br.com.superdia.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class NotaFiscal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Usuario cliente;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "notaFiscal")
    private List<Item> itens = new ArrayList<Item>();
    
    @Temporal(TemporalType.DATE)
    private Calendar data;
    
    public NotaFiscal() {}

	public NotaFiscal(Long id, Usuario cliente, List<Item> itens, Calendar data) {
		super();
		this.id = id;
		this.cliente = cliente;
		this.itens = itens;
		this.data = data;
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

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return String.format("NotaFiscal [id=%s, cliente=%s, itens=%s, data=%s]", id, cliente, itens, data);
	}
	
	public String toJson() {
	    return String.format(
	        "{\"id\":%d,\"cliente\":%s,\"itens\":[%s],\"data\":%d}",
	        id,
	        cliente != null ? cliente.toJson() : "null",
	        itens.stream().map(Item::toJson).collect(Collectors.joining(",")),
	        data != null ? data.getTimeInMillis() : 0
	    );
	}

}
