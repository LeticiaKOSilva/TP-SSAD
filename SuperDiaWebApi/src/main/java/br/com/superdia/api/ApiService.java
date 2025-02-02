package br.com.superdia.api;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class ApiService {
	@GET
	@Path("/hello")
	@Produces(MediaType.APPLICATION_JSON)
	public String hello() {
		return "{\"message\": \"Hello from SuperDia Web API!\"}";
	}
}