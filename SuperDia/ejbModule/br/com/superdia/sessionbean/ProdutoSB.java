package br.com.superdia.sessionbean;

import java.util.List;

import br.com.superdia.interfaces.IProduto;
import br.com.superdia.modelo.Produto;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateful
@Remote(IProduto.class)
public class ProdutoSB implements IProduto {
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public void create(Produto produto) {
		em.persist(produto);
	}

	@Override
	public void delete(Produto produto) {
		em.remove(em.merge(produto));
	}

	@Override
	public void update(Produto produto) {
		em.merge(produto);
	}

	@Override
	public List<Produto> getProdutos() {
		TypedQuery<Produto>produtos = em.createQuery("SELECT p FROM Produto p",Produto.class);
		return produtos.getResultList();
	}

}
