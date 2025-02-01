package br.com.superdia.interfaces;

import java.util.List;

import br.com.superdia.modelo.Produto;


public interface IProduto {
	public void create(Produto produto);
	public void delete(Produto produto);
	public void update(Produto produto);
	public List<Produto>getProdutos();
}
