package com.hcl.springgraphQL.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.springgraphQL.dao.PersonRepository;
import com.hcl.springgraphQL.entity.Person;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
/**
 * 
 * @author saikumar.yadda
 *@since 3/12/2021
 */
@RestController
public class PersonController {
	@Autowired
	private PersonRepository repo;

	@Value("classpath:person.graphqls")
	private Resource schemaResource;

	private GraphQL graphQL;

	@PostConstruct
	public void loadSchema() throws IOException {
		
		File schemaFile = schemaResource.getFile();
//parse the person.graphQl file
		TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);

		RuntimeWiring wiring = buildWiring();
		GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
		graphQL = GraphQL.newGraphQL(schema).build();
	}

	private RuntimeWiring buildWiring() {
		DataFetcher<List<Person>> fetcher1 = data -> {
			return repo.findAll();
		};
		DataFetcher<Person> fetcher2 = data -> {
			return repo.findByEmail(data.getArgument("email"));
		};
		DataFetcher<Person> fetcher3 = data -> {
			return repo.findByName(data.getArgument("name"));
		};
		return RuntimeWiring.newRuntimeWiring().type("Query",
				typeWriting -> typeWriting.dataFetcher("getAllPerson", fetcher1).dataFetcher("findPerson", fetcher2).dataFetcher("findPersonByName", fetcher3))
				.build();

	}

	@GetMapping("/getPersons")
	public List<Person> getAllperson() {
		return repo.findAll();
	}

	@PostMapping("/savePerson")
	public String addPerson(@RequestBody List<Person> persons) {
		repo.saveAll(persons);
		return "records inserted :" + persons.size();
	}

	@PostMapping("/getAll")
	public ResponseEntity<Object> findAllPerson(@RequestBody String query) {
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result, HttpStatus.OK);

	}

	@PostMapping("/getPersonByEmailId")
	public ResponseEntity<Object> getPersonByEmailId(@RequestBody String query) {
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result, HttpStatus.OK);

	}
	@PostMapping("/getPersonByName")
	public ResponseEntity<Object> getPersonByName(@RequestBody String query) {
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result, HttpStatus.OK);

	}

}
