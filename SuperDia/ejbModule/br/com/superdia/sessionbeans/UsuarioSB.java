package br.com.superdia.sessionbeans;

import java.util.List;

import br.com.superdia.interfaces.IUsuario;
import br.com.superdia.modelo.Usuario;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateful;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateful
@Remote(IUsuario.class)
public class UsuarioSB implements IUsuario {
	@PersistenceContext(unitName = "SuperDia")
	private EntityManager em;
	
	@Override
	public void create(Usuario usuario) {
		em.persist(usuario);
		em.flush();
	}

	@Override
	public void update(Usuario usuario) {
		em.merge(usuario);
	}

	@Override
	public void delete(Usuario usuario) {
		em.remove(em.merge(usuario));
	}

	@Override
	public List<Usuario> getUsuarios() {
		TypedQuery<Usuario>usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
		return usuarios.getResultList();
	}
	
	@Override
	public Usuario authentication(String email, String senha) {
	    TypedQuery<Usuario> query = em.createQuery(
	        "SELECT u FROM Usuario u WHERE u.pessoa.email = :email AND u.senha = :senha", 
	        Usuario.class
	    );
	    query.setParameter("email", email);
	    query.setParameter("senha", senha);
	    
	    List<Usuario> resultList = query.getResultList();
	    
	    return resultList.isEmpty() ? null : resultList.get(0);
	}

	@Override
	public Usuario getByEmail(String email) {
		TypedQuery<Usuario> query = em.createQuery(
				"SELECT u FROM Usuario u WHERE u.pessoa.email = :email", Usuario.class);
		query.setParameter("email", email);

		List<Usuario> resultList = query.getResultList();

		return resultList.isEmpty() ? null : resultList.get(0);
	}

}
