package ua.mai.zyme.graphql.zoo.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import graphql.GraphQLError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResultForAnimal {
    List<Animal> data;
    List<GraphQLError> errors;
}
