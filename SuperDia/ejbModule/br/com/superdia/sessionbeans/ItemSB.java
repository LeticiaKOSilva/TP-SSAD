package br.com.superdia.sessionbeans;

import java.util.List;

import br.com.superdia.interfaces.IItem;
import br.com.superdia.modelo.Item;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Remote(IItem.class)
public class ItemSB implements IItem {
	@PersistenceContext(unitName = "SuperDia")
	private EntityManager em;
	
	@Override
	public void create(Item item) {
		em.persist(item);
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

}
