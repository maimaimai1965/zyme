package ua.mai.graphql.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.INTERNAL_ERROR)        // Error classification
                .message(ex.getMessage())                   // Overrides the message
                .path(env.getExecutionStepInfo().getPath())
                .build();
//        if (ex instanceof VehicleNotFoundException) {
//            return GraphqlErrorBuilder.newError()
//                    .errorType(ErrorType.NOT_FOUND)
//                    .message(ex.getMessage())
//                    .path(env.getExecutionStepInfo().getPath())
//                    .location(env.getField().getSourceLocation())
//                    .build();
//        } else {
//            return null;
//        }
    }
}
