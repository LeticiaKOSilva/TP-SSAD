package br.com.superdia.api;

import java.io.Serializable;
import java.util.List;

import br.com.superdia.interfaces.IPessoa;
import br.com.superdia.interfaces.IUsuario;
import br.com.superdia.modelo.Pessoa;
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

@Path("/pessoa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped  // Garantir que o CDI gerencie o escopo
public class PessoaResource implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String PERFIL_ADMIN= "admin";
	
	@EJB
    private IPessoa pessoaService;
	
	@EJB
	private IUsuario usuarioService;
	
	public String getPerfil(String login,String senha) {
		Usuario usuario = usuarioService.authentication(login, senha);
		if (usuario != null) return usuario.getPerfil();
		return "";
	}
	
    @POST
    @Path("/create")
    public Response create(AuthRequest authRequest) {
        try {
       	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
       		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	
        	Pessoa pessoa = authRequest.getPessoa();
        	Pessoa createdPessoa = pessoaService.create(pessoa);
        	return Response.status(Response.Status.CREATED).entity(createdPessoa).build();
        	
        }catch (Exception e) {
        	return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar pessoa").build();
		}
       
    }

    @PUT
    @Path("/update")
    public Response update(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	Pessoa pessoa = authRequest.getPessoa();
            pessoaService.update(pessoa);
            return Response.ok(pessoa).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar pessoa").build();
        }
    }

    @POST
    @Path("/delete")
    public Response delete(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	
            Pessoa pessoa = authRequest.getPessoa();
            pessoaService.delete(pessoa);
            return Response.ok().entity("Pessoa removida com sucesso!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao remover pessoa").build();
        }
    }

    @POST
    @Path("/getPessoas")
    public Response getPessoas(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	
            List<Pessoa> pessoas = pessoaService.getPessoas();
            return Response.ok(pessoas).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar pessoas").build();
        }
    }

    public static class AuthRequest {
        private String login;
        private String senha;
        private Pessoa pessoa;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public Pessoa getPessoa() { return pessoa; }
        public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }
    }
}
