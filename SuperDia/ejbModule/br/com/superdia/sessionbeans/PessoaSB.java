package br.com.superdia.sessionbeans;

import java.util.List;

import br.com.superdia.interfaces.IPessoa;
import br.com.superdia.modelo.Pessoa;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;

@Stateful
@Remote(IPessoa.class)
public class PessoaSB implements IPessoa {

	@PersistenceContext(unitName = "SuperDia")
	EntityManager em;

	@Override
	public Pessoa create(Pessoa pessoa) {
		em.persist(pessoa);
		em.flush();
		return pessoa;
	}

	@Override
	public void delete(Pessoa pessoa) {
		em.remove(em.merge(pessoa));
	}

	@Override
	public void update(Pessoa pessoa) {
		em.merge(pessoa);

	}

	@Override
	public List<Pessoa> getPessoas() {
		CriteriaQuery<Pessoa> query = em.getCriteriaBuilder().createQuery(Pessoa.class);
		query.select(query.from(Pessoa.class));
		List<Pessoa> pessoas = em.createQuery(query).getResultList();
		return pessoas;
	}

}
