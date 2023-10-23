package ua.mai.zyme.graphql.zoo.models;

import java.util.Arrays;
import java.util.List;

public record Book (String id, String name, int pageCount, String authorId) {

}
