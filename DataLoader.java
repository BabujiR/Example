package com.equal.exportapi.util;

import com.equal.exportapi.entity.Person;
import com.equal.exportapi.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final PersonRepository repo;

    public DataLoader(PersonRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        repo.save(new Person("Alice Johnson", 34));
        repo.save(new Person("Bob Smith", 29));
        repo.save(new Person("Charlie Brown", 42));
    }
}
