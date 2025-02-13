package br.com.superdia.interfaces;

import br.com.superdia.modelo.Carrinho;
import jakarta.ejb.Remote;

@Remote
public interface ICarrinho {
	public Carrinho create(Carrinho carrinho);
	public void delete(Carrinho carrinho);
	public void update(Carrinho carrinho);
	public Carrinho getCarrinhoByUsuario(Long usuarioId);
	public Carrinho getCarrinhoById(Long id);
}
