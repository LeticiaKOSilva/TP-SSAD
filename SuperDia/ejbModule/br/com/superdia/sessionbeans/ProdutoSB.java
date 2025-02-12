package br.com.superdia.sessionbeans;

import java.util.List;

import br.com.superdia.interfaces.IProduto;
import br.com.superdia.modelo.Produto;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Remote(IProduto.class)
public class ProdutoSB implements IProduto {
	@PersistenceContext(unitName = "SuperDia")
	private EntityManager em;

	@Override
	public Produto create(Produto produto) {
		em.persist(produto);
		em.flush();
		return produto;
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
		TypedQuery<Produto> produtos = em.createQuery("SELECT p FROM Produto p", Produto.class);
		return produtos.getResultList();
	}

	@Override
	public Produto getById(Long id) {
		TypedQuery<Produto> query = em.createQuery(
				"SELECT p FROM Produto p WHERE p.id = :id", Produto.class);
		query.setParameter("id", id);

		List<Produto> resultList = query.getResultList();

		return resultList.isEmpty() ? null : resultList.get(0);
	}

}
