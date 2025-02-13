package br.com.superdia.api;

import java.io.Serializable;
import java.util.List;

import br.com.superdia.interfaces.IUsuario;
import br.com.superdia.modelo.Usuario;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/usuario")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped  // Garantir que o CDI gerencie o escopo
public class UsuarioResource implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String PERFIL_ADMIN= "admin";
	
	@EJB
    private IUsuario usuarioService;
	
	public String getPerfil(String login,String senha) {
		Usuario usuario = usuarioService.authentication(login, senha);
		if (usuario != null) return usuario.getPerfil();
		return "";
	}
	
    public boolean isAuthenticated(String login, String senha) {
        return usuarioService.authentication(login, senha) != null;
    }
	
    @POST
    @Path("/create")
    public Response create(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	
        	Usuario usuario = authRequest.getUsuario();
        	usuarioService.create(usuario);
        	return Response.status(Response.Status.CREATED).entity(usuario).build();
        	
        }catch (Exception e) {
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar usuario").build();
		}
    }

    @PUT
    @Path("/update")
    public Response update(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	Usuario usuario = authRequest.getUsuario();
            usuarioService.update(usuario);
            return Response.ok(usuario).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar usuario").build();
        }
    }

    @POST
    @Path("/delete")
    public Response delete(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	
            Usuario usuario = authRequest.getUsuario();
            usuarioService.delete(usuario);
            return Response.ok().entity("Usuario removido com sucesso!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao remover usuario").build();
        }
    }

    @POST
    @Path("/getPessoas")
    public Response getPessoas(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	
            List<Usuario> usuarios = usuarioService.getUsuarios();
            return Response.ok(usuarios).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar usuarios").build();
        }
    }
    
    @POST
    @Path("/authenticate")
    public Response authenticate(AuthRequest authRequest) {
        try {
            Usuario usuario = usuarioService.authentication(authRequest.getLogin(), authRequest.getSenha());

            if (usuario != null) {
                return Response.ok(usuario).build();
            } else {
                return Response.status(Response.Status.FORBIDDEN).entity("Credenciais inválidas!").build();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro na autenticação").build();
        }
    }
    
    @POST
    @Path("/getByEmail")
    public Response getById(AuthRequest authRequest) {
        try {
        	if(!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	
        	Usuario usuario = usuarioService.getByEmail(authRequest.getUsuario().getPessoa().getEmail());
            return Response.ok(usuario).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar usuarios").build();
        }
    }

    public static class AuthRequest {
        private String login;
        private String senha;
        private Usuario usuario;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public Usuario getUsuario() { return usuario; }
        public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    }
}
