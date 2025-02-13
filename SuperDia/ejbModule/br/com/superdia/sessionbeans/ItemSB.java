package br.com.superdia.sessionbeans;

import java.util.List;

import br.com.superdia.interfaces.IItem;
import br.com.superdia.modelo.Item;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Remote(IItem.class)
public class ItemSB implements IItem {
	@PersistenceContext(unitName = "SuperDia")
	private EntityManager em;
	
	@Override
	public Item create(Item item) {
		em.persist(item);
		em.flush();
		return item;
	}

	@Override
	public void delete(Item item) {
		em.remove(em.merge(item));
	}

	@Override
	public void update(Item item) {
		em.merge(item);
	}

	@Override
	public List<Item> getProdutos() {
		TypedQuery<Item> itens = em.createQuery("SELECT p FROM Item p", Item.class);
		return itens.getResultList();
	}

	@Override
	public Item getItemById(Long id) {
	    TypedQuery<Item> query = em.createQuery(
		        "SELECT i FROM Item i WHERE i.id = :id", Item.class);
		    query.setParameter("id", id);
		    
		    try {
		        return query.getSingleResult();
		    } catch (NoResultException e) {
		        return null;
		    }
	}
}
