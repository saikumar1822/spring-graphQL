package com.hcl.springgraphQL.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcl.springgraphQL.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Integer> {

	Person findByEmail(String email);

	Person findByName(String name);

}
