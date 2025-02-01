package br.com.superdia.sessionbean;

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
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public void create(Usuario usuario) {
		em.persist(usuario);
	}

	@Override
	public void update(Usuario usuario) {
		em.merge(usuario);
	}

	@Override
	public void remove(Usuario usuario) {
		em.remove(em.merge(usuario));
	}

	@Override
	public List<Usuario> getUsuarios() {
		TypedQuery<Usuario>usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
		return usuarios.getResultList();
	}
	
	@Override
	public Usuario autentication(Usuario usuario) {
		TypedQuery<Usuario>query = em.createQuery("SELECT u FROM Usuario u WHERE u.perfil=:perfil AND u.senha=:usuario", Usuario.class);
		query.setParameter("perfil", usuario.getPerfil());
		query.setParameter("senha",usuario.getSenha());
		List<Usuario>resuList = query.getResultList();
		
		if(resuList.isEmpty())
			return null;
		return resuList.get(0);
	}
}
