package br.com.superdia.api;

import br.com.superdia.interfaces.IProduto;
import br.com.superdia.interfaces.IUsuario;
import br.com.superdia.modelo.Produto;
import br.com.superdia.modelo.Usuario;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.util.List;

@Path("/produto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped  // Garantir que o CDI gerencie o escopo
public class ProdutoResource implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String PERFIL_ADMIN= "admin";
	
	@EJB
    private IProduto produtoService;
	
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
        	Produto produto = authRequest.getProduto();
        	
            Produto createdProduto = produtoService.create(produto);
            return Response.status(Response.Status.CREATED).entity(createdProduto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar produto").build();
        }
    }

    @PUT
    @Path("/update")
    public Response update(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	Produto produto = authRequest.getProduto();
        	
            produtoService.update(produto);
            return Response.ok(produto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar produto").build();
        }
    }

    @POST
    @Path("/delete")
    public Response delete(AuthRequest authRequest) {
        try {
        	if(!getPerfil(authRequest.getLogin(), authRequest.getSenha()).equals(PERFIL_ADMIN))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	Produto produto = authRequest.getProduto();
        	
            produtoService.delete(produto);
            return Response.ok().entity("Produto removido com sucesso!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao remover produto").build();
        }
    }
    
    @GET
    @Path("/")
    public Response get() {
        try {
            List<Produto> produtos = produtoService.getProdutos();
            return Response.ok(produtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar produtos").build();
        }
    }

    @POST
    @Path("/getProdutos")
    public Response getProdutos(AuthRequest authRequest) {
        try {
        	if(!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
        		return Response.status(Response.Status.FORBIDDEN).entity("Acesso Negado! Você não pode realizar essa operação").build();
        	
            List<Produto> produtos = produtoService.getProdutos();
            return Response.ok(produtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar produtos").build();
        }
    }

    public static class AuthRequest {
        private String login;
        private String senha;
        private Produto produto;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public Produto getProduto() { return produto; }
        public void setProduto(Produto produto) { this.produto = produto; }
    }
}
