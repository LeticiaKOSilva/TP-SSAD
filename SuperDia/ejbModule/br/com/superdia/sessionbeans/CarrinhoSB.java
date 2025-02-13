package br.com.superdia.sessionbeans;

import br.com.superdia.interfaces.ICarrinho;
import br.com.superdia.modelo.Carrinho;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Remote(ICarrinho.class)
public class CarrinhoSB implements ICarrinho {
	@PersistenceContext(unitName = "SuperDia")
	private EntityManager em;
	
	@Override
	public Carrinho create(Carrinho carrinho) {
		em.persist(carrinho);
		em.flush();
		return carrinho;
	}

	@Override
	public void delete(Carrinho carrinho) {
		em.remove(em.merge(carrinho));
	}

	@Override
	public void update(Carrinho carrinho) {
		em.merge(carrinho);
	}

	@Override
	public Carrinho getCarrinhoByUsuario(Long usuarioId) {
	    TypedQuery<Carrinho> query = em.createQuery(
	        "SELECT c FROM Carrinho c WHERE c.cliente.id = :clienteId", Carrinho.class);
	    query.setParameter("clienteId", usuarioId);
	    
	    try {
	        return query.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	@Override
	public Carrinho getCarrinhoById(Long id) {
	    TypedQuery<Carrinho> query = em.createQuery(
		        "SELECT c FROM Carrinho c WHERE c.id = :id", Carrinho.class);
		    query.setParameter("id", id);
		    
		    try {
		        return query.getSingleResult();
		    } catch (NoResultException e) {
		        return null;
		    }
	}
}
