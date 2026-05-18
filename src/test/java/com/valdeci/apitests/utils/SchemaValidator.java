package com.valdeci.apitests.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.hamcrest.Matcher;

/**
 * Utilitário para validação de contratos JSON Schema.
 * Os schemas ficam em src/test/resources/schemas/
 */
public class SchemaValidator {

    public static Matcher<?> validateSchema(String schemaFileName) {
        return JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + schemaFileName);
    }
}
