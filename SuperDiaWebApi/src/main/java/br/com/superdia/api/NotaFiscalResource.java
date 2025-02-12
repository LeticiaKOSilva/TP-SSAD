package br.com.superdia.api;

import java.io.Serializable;
import java.util.List;

import br.com.superdia.interfaces.INotaFiscal;
import br.com.superdia.interfaces.IUsuario;
import br.com.superdia.modelo.NotaFiscal;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/notafiscal")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped  
public class NotaFiscalResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private INotaFiscal notaFiscalService;

    @EJB
    private IUsuario usuarioService;

    public boolean isAuthenticated(String login, String senha) {
        return usuarioService.authentication(login, senha) != null;
    }

    @POST
    @Path("/create")
    public Response create(AuthRequest authRequest) {
        try {
            if (!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("Acesso Negado! Autenticação necessária").build();
            
            NotaFiscal notaFiscal = authRequest.getNotaFiscal();
            notaFiscalService.create(notaFiscal);
            return Response.status(Response.Status.CREATED).entity(notaFiscal).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao criar nota fiscal").build();
        }
    }

    @PUT
    @Path("/update")
    public Response update(AuthRequest authRequest) {
        try {
            if (!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("Acesso Negado! Autenticação necessária").build();
            
            NotaFiscal notaFiscal = authRequest.getNotaFiscal();
            notaFiscalService.update(notaFiscal);
            return Response.ok(notaFiscal).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar nota fiscal").build();
        }
    }

    @POST
    @Path("/delete")
    public Response delete(AuthRequest authRequest) {
        try {
            if (!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("Acesso Negado! Autenticação necessária").build();
            
            NotaFiscal notaFiscal = authRequest.getNotaFiscal();
            notaFiscalService.delete(notaFiscal);
            return Response.ok().entity("Nota fiscal removida com sucesso!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao remover nota fiscal").build();
        }
    }

    @POST
    @Path("/getNotas")
    public Response getNotas(AuthRequest authRequest) {
        try {
            if (!isAuthenticated(authRequest.getLogin(), authRequest.getSenha()))
                return Response.status(Response.Status.UNAUTHORIZED).entity("Acesso Negado! Autenticação necessária").build();
            
            List<NotaFiscal> notas = notaFiscalService.getProdutos();
            return Response.ok(notas).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar notas fiscais").build();
        }
    }

    public static class AuthRequest {
        private String login;
        private String senha;
        private NotaFiscal notaFiscal;

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }

        public NotaFiscal getNotaFiscal() { return notaFiscal; }
        public void setNotaFiscal(NotaFiscal notaFiscal) { this.notaFiscal = notaFiscal; }
    }
}
