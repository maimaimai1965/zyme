package ua.mai.zyme.graphql.zoo.controllers;

import graphql.GraphQLError;
import graphql.execution.DataFetcherResult;
import graphql.servlet.GenericGraphQLError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import ua.mai.zyme.db.zo.schema.TankType;
import ua.mai.zyme.graphql.zoo.models.Animal;
import ua.mai.zyme.graphql.zoo.models.QueryResultForAnimal;
import ua.mai.zyme.graphql.zoo.models.Tank;
import ua.mai.zyme.graphql.zoo.models.TankAnimal;
import ua.mai.zyme.graphql.zoo.services.ZooService;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Controller
public class ZooController {

    @Autowired
    private ZooService zooService;


    // ---------------- Animal ---------------
    @SchemaMapping(typeName = "Animal", field = "currTank")
    public Tank currTankInAnimal(Animal animal) {
        if (animal == null)
            return null;
        return currTankByAnimalId(animal.getAnimalId().toString());
    }

    @SchemaMapping(typeName = "Animal", field = "tanks")
    public List<Tank> tanksInAnimal(Animal animal) {
        if (animal == null)
            return Collections.<Tank>emptyList();
        return tanksByAnimalId(animal.getAnimalId().toString());
    }

    @QueryMapping
    public List<Animal> allAnimal() {
        return zooService.allAnimal();
    }

//    @QueryMapping
//    public DataFetcherResult<Object> allAnimalWithErrors2() {
//        List<Animal> list = zooService.allAnimal();
//        List<GraphQLError> errors =
//                List.of(new GenericGraphQLError("My exception 1"), new GenericGraphQLError("My exception 1"));
//        // Создание объекта DataFetcherResult с результатами
//        return QueryResultForAnimal.<List<Animal>>builder()
//                .data(list)
//                .errors(errors)
////                .extensions(extensions)
//                .build();
//    }
    @QueryMapping
    public QueryResultForAnimal allAnimalWithErrors() {
        List<Animal> list = zooService.allAnimal();
        List<GraphQLError> errors =
                List.of(new GenericGraphQLError("My exception 1"), new GenericGraphQLError("My exception 1"));
        // Создание объекта DataFetcherResult с результатами
        return QueryResultForAnimal.<List<Animal>>builder()
                .data(list)
                .errors(errors)
//                .extensions(extensions)
                .build();
    }


    @QueryMapping
    public Animal animalByAnimalId(@Argument Long animalId) {
        throw new RuntimeException("My exception");
//        return zooService.animalByAnimalId(animalId);
    }

    @QueryMapping
    public List<Animal> animals(@Argument Long animalId, @Argument String likeNickname, @Argument Integer animalTypeId) {
        return zooService.animals(animalId, likeNickname, animalTypeId);
    }

    @QueryMapping
    public List<Animal> currAnimalsByTankId(@Argument @NotNull String tankId) {
        Integer intTankId = tankId != null ? Integer.parseInt(tankId) : null;
        return zooService.currAnimalsByTankId(intTankId);
    }

    @QueryMapping
    public List<Animal> animalsByTankId(@Argument @NotNull String tankId) {
        Integer intTankId = tankId != null ? Integer.parseInt(tankId) : null;
        return zooService.animalsByTankId(intTankId);
    }


    // ---------------- Tank ---------------
    @SchemaMapping(typeName = "Tank", field = "currAnimals")
    public List<Animal> currAnimalsInTank(Tank tank) {
        if (tank == null)
            return Collections.<Animal>emptyList();
        return currAnimalsByTankId(tank.getTankId().toString());
    }

    @SchemaMapping(typeName = "Tank", field = "animals")
    public List<Animal> animalsInTank(Tank tank) {
        if (tank == null)
            return Collections.<Animal>emptyList();
        return animalsByTankId(tank.getTankId().toString());
    }

    @QueryMapping
    public List<Tank> tanks(@Argument String tankId, @Argument TankType tankType, @Argument String numberCd,
                            @Argument String likeNumberCd, @Argument String likeDescr) {
        Integer intTankId = tankId != null ? Integer.parseInt(tankId) : null;
        return zooService.tanks(intTankId, tankType, numberCd, likeNumberCd, likeDescr);
    }

    @QueryMapping
    public Tank tankByTankId(@Argument Integer tankId) {
//        Integer intId = tankId != null ? Integer.parseInt(tankId) : null;
        return zooService.tankByTankId(tankId);
    }

    @QueryMapping
    public Tank currTankByAnimalId(@Argument String animalId) {
        Long longAnimalId = animalId != null ? Long.parseLong(animalId) : null;
        return zooService.currTankByAnimalId(longAnimalId);
    }

    @QueryMapping
    public List<Tank> tanksByAnimalId(@Argument String animalId) {
        Long longAnimalId = animalId != null ? Long.parseLong(animalId) : null;
        return zooService.tanksByAnimalId(longAnimalId);
    }

    // ---------------- TankAnimal ---------------
    @SchemaMapping(typeName = "TankAnimal", field = "tank")
    public Tank tankInTankAnimal(TankAnimal tankAnimal) {
        return zooService.tankByTankId(tankAnimal.getTankId()) ;
    }

    @SchemaMapping(typeName = "TankAnimal", field = "animal")
    public Animal animalInTankAnimal(TankAnimal tankAnimal) {
        return zooService.animalByAnimalId(tankAnimal.getAnimalId());
    }

    @QueryMapping
    public List<TankAnimal> allTankAnimal() {
        return zooService.allTankAnimal();
    }

}

