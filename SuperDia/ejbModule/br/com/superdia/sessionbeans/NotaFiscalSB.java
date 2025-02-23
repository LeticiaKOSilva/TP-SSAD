package br.com.superdia.sessionbeans;

import java.util.List;

import br.com.superdia.interfaces.INotaFiscal;
import br.com.superdia.modelo.Item;
import br.com.superdia.modelo.NotaFiscal;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Remote(INotaFiscal.class)
public class NotaFiscalSB implements INotaFiscal {
	@PersistenceContext(unitName = "SuperDia")
	private EntityManager em;
	
	@Override
	public void create(NotaFiscal notaFiscal) {
		for (int i = 0; i < notaFiscal.getItens().size(); i++) {
		    Item item = notaFiscal.getItens().get(i);
		    if (item.getId() != null) {
		        notaFiscal.getItens().set(i, em.merge(item));
		    }
		}
		
		em.persist(notaFiscal);
	}

	@Override
	public void delete(NotaFiscal notaFiscal) {
		em.remove(em.merge(notaFiscal));
	}

	@Override
	public void update(NotaFiscal notaFiscal) {
		em.merge(notaFiscal);
	}

	@Override
	public List<NotaFiscal> getProdutos() {
		TypedQuery<NotaFiscal> itens = em.createQuery("SELECT p FROM NotaFiscal p", NotaFiscal.class);
		return itens.getResultList();
	}

}
