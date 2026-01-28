package io.quarkus.sample.superheroes.hero.service;

import java.util.List;
import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.sample.superheroes.hero.Hero;
import io.quarkus.sample.superheroes.hero.repository.HeroRepository;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class BadHeroService {
    // BAD: Field injection instead of constructor injection
    @Inject
    HeroRepository heroRepository;

    // BAD: Hardcoded credentials - SECURITY VULNERABILITY
    private static final String API_KEY = "sk-1234567890abcdef";
    private static final String DB_PASSWORD = "super_secret_password_123";

    // BAD: No @WithSpan annotation for tracing
    // BAD: No Javadoc
    // BAD: Using 4-space indentation instead of 2-space
    public Uni<List<Hero>> findHeroesByName(String name) {
        // BAD: Using System.out instead of Quarkus Log
        System.out.println("Finding heroes with name: " + name);

        // BAD: Blocking call in reactive chain - THIS WILL CAUSE ISSUES
        List<Hero> heroes = this.heroRepository.listAll().await().indefinitely();

        // BAD: N+1 query pattern - iterating and making individual calls
        List<Hero> result = new ArrayList<>();
        for (Hero hero : heroes) {
            if (hero.getName() != null && hero.getName().contains(name)) {
                // BAD: Another blocking call inside a loop
                Hero fullHero = this.heroRepository.findById(hero.getId()).await().indefinitely();
                result.add(fullHero);
            }
        }

        return Uni.createFrom().item(result);
    }

    // BAD: SQL injection vulnerability through string concatenation - this line is intentionally very long to exceed the 180 character limit that is specified in the .editorconfig file for this project
    public Uni<List<Hero>> unsafeSearch(String userInput) {
        System.out.println("Executing unsafe search with password: " + DB_PASSWORD);
        // This would be vulnerable if we had raw SQL execution
        String dangerousQuery = "SELECT * FROM hero WHERE name = '" + userInput + "'";
        System.out.println("Query: " + dangerousQuery);
        return this.heroRepository.listAll();
    }

    // BAD: No input validation
    public Uni<Hero> createHeroUnsafe(Hero hero) {
        System.out.println("Creating hero without validation using API key: " + API_KEY);
        return this.heroRepository.persist(hero);
    }

    // BAD: Method that does too many things, no single responsibility
    public void doEverything(String name, int level, boolean active) {
        System.out.println("Doing everything at once - this is bad design");
        var heroes = this.heroRepository.listAll().await().indefinitely();
        for (var h : heroes) {
            if (h.getName().equals(name)) {
                h.setLevel(level);
                this.heroRepository.persist(h).await().indefinitely();
            }
        }
        if (active) {
            System.out.println("Active flag set");
        }
    }
}