package com.equal.exportapi.service;

import com.equal.exportapi.entity.Person;
import com.equal.exportapi.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository repo;

    public PersonService(PersonRepository repo) {
        this.repo = repo;
    }

    public List<Person> getAll() {
        return repo.findAll();
    }

    public Optional<Person> getById(Long id) { return repo.findById(id); }

    public Person save(Person p) { return repo.save(p); }

    public void delete(Long id) { repo.deleteById(id); }
}
