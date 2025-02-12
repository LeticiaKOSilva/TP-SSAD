package br.com.superdia.interfaces;

import java.util.List;

import br.com.superdia.modelo.Produto;
import jakarta.ejb.Remote;

@Remote
public interface IProduto {
	public Produto create(Produto produto);
	public void delete(Produto produto);
	public void update(Produto produto);
	public List<Produto>getProdutos();
	public Produto getById(Long id);
}
