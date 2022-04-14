package com.deloitte.elrr.elrrconsolidate.consumer;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@RestController
@Configuration
public class Neo4jController {

	private final Driver driver;

	@Value("${courseCSV}")
	String courseCSV;

	@Value("${categoryCSV}")
	String categoryCSV;

	@Value("${learnerCSV}")
	String learnerCSV;

	@Value("${xapiJSON}")
	String xapiJSON;

	public Neo4jController(Driver driver) {
		this.driver = driver;
	}

	@GetMapping(path = "/nodes", produces = MediaType.APPLICATION_JSON_VALUE)
	private void createOrUpdateNode() {

		try (Session session = driver.session()) {

			// Create Courses nodes using csv file
			session.run("LOAD CSV WITH HEADERS FROM ' " + courseCSV + " ' AS row" +
					" WITH row WHERE row.Name IS NOT NULL " +
					"MERGE (x:Courses {Name: row.Name,Title: row.Title, Mode: row.Mode,Duration: row.Duration, CategoryID: row.Id});");

			// Create Categories nodes using csv file
			session.run("LOAD CSV WITH HEADERS FROM ' " + categoryCSV + " ' AS row " +
					" WITH row WHERE row.Name IS NOT NULL " +
					" MERGE (y:Category {Name: row.Name});");

			// Create Learner nodes using csv file
			session.run("LOAD CSV WITH HEADERS FROM ' " + learnerCSV + " ' AS row " +
					" WITH row WHERE row.id IS NOT NULL " +
					" MERGE (z:Learner {Name: row.id, id: row.id,firstName: row.firstName, Lastname: row.Lastname,email: row.email, "
					+
					" Employment: row.Employment,Role: row.Role,Orginatization:row.Orginatization, Sex: row.Sex,DOB: row.DOB });");

			// Creating relationships between courses and categories
			session.run("MATCH" +
					" (x:Courses)," +
					" (y:Category)" +
					" WHERE x.CategoryID = y.Name" +
					" MERGE (x)-[r:IS_IN]->(y)" +
					" RETURN type(r)");

			// Merging or Creating relationships between Learner and courses using XAPI Json
			// file
			session.run("CALL apoc.load.json('" + xapiJSON + "') " +
					" YIELD value AS v " +
					" WITH v.statements as st " +
					" UNWIND st as s " +
					" WITH s " +

					" MATCH " +
					" (x:Courses), " +
					" (z:Learner) " +
					" WHERE z.id = s.actor.name AND x.Title=s.object.definition.name['en-US'] " +
					" CALL apoc.merge.relationship(z, s.verb.display['en-US'], NULL,{created: datetime()}, x, {lastSeen: datetime()}) YIELD rel "
					+
					" RETURN z.id, type(rel), x.name ");
		}
	}

}