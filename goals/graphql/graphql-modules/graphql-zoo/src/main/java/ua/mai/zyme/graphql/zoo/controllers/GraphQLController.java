package ua.mai.zyme.graphql.zoo.controllers;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//@RestController
//@RequestMapping("/graphql")
public class GraphQLController {

//    private final GraphQL graphQL;
//
//    public GraphQLController(GraphQLSchema graphQLSchema) {
//        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
//    }
//
//    @PostMapping
//    public ResponseEntity<Object> graphql(@RequestBody Map<String, Object> request) {
//        ExecutionResult executionResult = graphQL.execute(ExecutionInput.newExecutionInput()
//                .query((String) request.get("query"))
//                .operationName((String) request.get("operationName"))
//                .variables((Map<String, Object>) request.get("variables"))
//                .build());
//
//        return ResponseEntity.ok(executionResult.toSpecification());
//    }
}