package br.com.superdia.api;

import br.com.superdia.interfaces.IProduto;
import br.com.superdia.modelo.Produto;
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
	
	@EJB
    private IProduto produtoService;

    @POST
    @Path("/create")
    public Response create(Produto produto) {
        try {
            produtoService.create(produto);
            return Response.status(Response.Status.CREATED).entity(produto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar produto").build();
        }
    }

    @PUT
    @Path("/update")
    public Response update(Produto produto) {
        try {
            produtoService.update(produto);
            return Response.ok(produto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar produto").build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            Produto produto = new Produto();
            produto.setId(id);
            produtoService.delete(produto);
            return Response.ok().entity("Produto removido com sucesso!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao remover produto").build();
        }
    }

    @GET
    @Path("/getProdutos")
    public Response getProdutos() {
        try {
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
