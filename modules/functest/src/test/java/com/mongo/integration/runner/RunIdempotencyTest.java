package com.mongo.integration.runner;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

/**
 * 
 * @author Marcos Rachid
 *
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty",
		"html:target/cucumber" }, glue = "com.mongo.integration.steps", features = "classpath:features/idempotency.feature")
public class RunIdempotencyTest {}
