package io.quarkus.sample.superheroes.hero.rest;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.quarkus.sample.superheroes.hero.Hero;
import io.quarkus.sample.superheroes.hero.service.BadHeroService;

import io.smallrye.mutiny.Uni;

// BAD: Missing @Tag for OpenAPI grouping
// BAD: Missing class-level Javadoc
@Path("/api/bad-heroes")
@Produces(MediaType.APPLICATION_JSON)
public class BadHeroResource {

    // BAD: Field injection
    @Inject
    BadHeroService badHeroService;

    // BAD: Missing @Operation annotation
    // BAD: Missing @APIResponse annotations
    // BAD: No pagination support for list endpoint
    // BAD: Returns raw list without wrapper
    @GET
    @Path("/search")
    public Uni<List<Hero>> searchHeroes(@QueryParam("name") String name) {
        // BAD: No input validation on query param
        return badHeroService.findHeroesByName(name);
    }

    // BAD: SQL injection vulnerable endpoint exposed
    @GET
    @Path("/unsafe-search")
    public Uni<List<Hero>> unsafeSearch(@QueryParam("q") String query) {
        return badHeroService.unsafeSearch(query);
    }

    // BAD: No @Valid annotation for request body validation
    // BAD: Returns 200 instead of 201 for creation
    @POST
    public Uni<Hero> createHero(Hero hero) {
        return badHeroService.createHeroUnsafe(hero);
    }

    // BAD: Blocking operation in REST endpoint
    // BAD: Catches generic Exception
    @GET
    @Path("/sync/{id}")
    public Response getHeroSync(@PathParam("id") Long id) {
        try {
            // BAD: This will block the event loop
            Thread.sleep(100);
            System.out.println("Fetched hero: " + id);
            return Response.ok().build();
        } catch (Exception e) {
            // BAD: Swallowing exception details
            return Response.serverError().build();
        }
    }

    // BAD: Endpoint with no purpose, dead code
    @GET
    @Path("/unused")
    public String unusedEndpoint() {
        return "This endpoint serves no purpose";
    }
}