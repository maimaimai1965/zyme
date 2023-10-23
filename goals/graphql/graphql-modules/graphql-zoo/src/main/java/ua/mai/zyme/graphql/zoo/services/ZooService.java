package ua.mai.zyme.graphql.zoo.services;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.db.zo.schema.TankType;
import ua.mai.zyme.graphql.zoo.models.Animal;
import ua.mai.zyme.graphql.zoo.models.Tank;
import ua.mai.zyme.graphql.zoo.models.TankAnimal;

import java.time.LocalDateTime;
import java.util.List;

import static ua.mai.zyme.db.zo.schema.Tables.*;


@Service
public class ZooService {

    private final Logger LOG = LoggerFactory.getLogger("zoo.ZooService");

    private DSLContext dslContext;

    public ZooService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }


    // ---------------- Animal ---------------
    @Transactional(readOnly = true)
    public Animal animalByAnimalId(Long animalId) {
        Animal animal = dslContext
                .selectFrom(ZO_ANIMAL)
                .where(ZO_ANIMAL.ANIMAL_ID.eq(animalId))
                .fetchOneInto(Animal.class);
        if (animal != null)
            animal.setTanks(tanksByAnimalId(animalId));

        LOG.debug("animalById(): id={}", animalId);
        return animal;
    }

    @Transactional(readOnly = true)
    public List<Animal> allAnimal() {
        List<Animal> list = dslContext
                .selectFrom(ZO_ANIMAL)
                .fetchInto(Animal.class);

        LOG.debug("getAnimals(): count={}", list.size());
        return list;
    }

    @Transactional(readOnly = true)
    public List<Animal> animals(Long id, String likeNickname, Integer animalTypeId) {
        List<Animal> list = dslContext
                .selectFrom(ZO_ANIMAL)
                .where(id==null ? DSL.noCondition() : ZO_ANIMAL.ANIMAL_ID.eq(id))
                  .and(likeNickname==null ? DSL.noCondition() : ZO_ANIMAL.NICKNAME.likeIgnoreCase("%" + likeNickname + "%"))
                  .and(animalTypeId==null ? DSL.noCondition() : ZO_ANIMAL.ANIMAL_TYPE_ID.eq(animalTypeId))
                .fetchInto(Animal.class);

        LOG.debug("animals(): count={}", list.size());
        return list;
    }

    @Transactional(readOnly = true)
    public List<Animal> currAnimalsByTankId(Integer tankId) {
        LocalDateTime currDate = getCurrDate();
        List<Animal> animals = dslContext
                .select(ZO_ANIMAL.fields())
                .from(ZO_ANIMAL)
                .join(ZO_TANK_ANIMAL).on(ZO_TANK_ANIMAL.ANIMAL_ID.eq(ZO_ANIMAL.ANIMAL_ID))
                .where(ZO_TANK_ANIMAL.TANK_ID.eq(tankId))
                  .and(ZO_TANK_ANIMAL.FROM_DT.lessOrEqual(DSL.value(currDate)))
                  .and(ZO_TANK_ANIMAL.TO_DT.isNull().or(ZO_TANK_ANIMAL.TO_DT.greaterThan(DSL.value(currDate))))
                .fetchInto(Animal.class);

        LOG.debug("currAnimalsByTankId(): tankId={}", tankId);
        return animals;
    }

    @Transactional(readOnly = true)
    public List<Animal> animalsByTankId(Integer tankId) {
        List<Animal> animals = dslContext
                .select(ZO_ANIMAL.fields())
                .from(ZO_ANIMAL)
                .join(ZO_TANK_ANIMAL).on(ZO_TANK_ANIMAL.ANIMAL_ID.eq(ZO_ANIMAL.ANIMAL_ID))
                .where(ZO_TANK_ANIMAL.TANK_ID.eq(tankId))
                .fetchInto(Animal.class);

        LOG.debug("animalsByTankId(): tankId={}", tankId);
        return animals;
    }

    // ---------------- Tank ---------------
    @Transactional(readOnly = true)
    public List<Tank> tanks(Integer id, TankType tankType, String numberCd, String likeNumberCd, String likeDescr) {
        List<Tank> list = dslContext
                .selectFrom(ZO_TANK)
                .where(id==null ? DSL.noCondition() : ZO_TANK.TANK_ID.eq(id))
                  .and(tankType==null ? DSL.noCondition() : ZO_TANK.TANK_TYPE.eq(tankType))
                  .and(numberCd==null ? DSL.noCondition() : ZO_TANK.NUMBER_CD.eq(numberCd))
                  .and(likeNumberCd==null ? DSL.noCondition() : ZO_TANK.NUMBER_CD.likeIgnoreCase("%" + likeNumberCd + "%"))
                  .and(likeDescr==null ? DSL.noCondition() : ZO_TANK.DESCR.likeIgnoreCase("%" + likeDescr + "%"))
                .fetchInto(Tank.class);

        LOG.debug("tanks(): count={}", list.size());
        return list;
    }

    @Transactional(readOnly = true)
    public Tank tankByTankId(Integer tankId) {
        Tank tank = dslContext
                .selectFrom(ZO_TANK)
                .where(ZO_TANK.TANK_ID.eq(tankId))
                .fetchOneInto(Tank.class);

        LOG.debug("tankById(): id={}", tankId);
        return tank;
    }

    @Transactional(readOnly = true)
    public Tank currTankByAnimalId(Long animalId) {
        LocalDateTime currDate = getCurrDate();
        Tank tank = dslContext
                .select(ZO_TANK.fields())
                .from(ZO_TANK)
                .join(ZO_TANK_ANIMAL).on(ZO_TANK_ANIMAL.TANK_ID.eq(ZO_TANK.TANK_ID))
                .where(ZO_TANK_ANIMAL.ANIMAL_ID.eq(animalId))
                  .and(ZO_TANK_ANIMAL.FROM_DT.lessOrEqual(DSL.value(currDate)))
                  .and(ZO_TANK_ANIMAL.TO_DT.isNull().or(ZO_TANK_ANIMAL.TO_DT.greaterThan(DSL.value(currDate))))
                .fetchOneInto(Tank.class);

        LOG.debug("currTankByAnimalId(): animalId={}", animalId);
        return tank;
    }

    @Transactional(readOnly = true)
    public List<Tank> tanksByAnimalId(Long animalId) {
        List<Tank> tanks = dslContext
                .select(ZO_TANK.fields())
                .from(ZO_TANK)
                .join(ZO_TANK_ANIMAL).on(ZO_TANK_ANIMAL.TANK_ID.eq(ZO_TANK.TANK_ID))
                .where(ZO_TANK_ANIMAL.ANIMAL_ID.eq(animalId))
                .fetchInto(Tank.class);

        LOG.debug("tanksByAnimalId(): animalId={}", animalId);
        return tanks;
    }


    // ---------------- TankAnimal ---------------
    @Transactional(readOnly = true)
    public List<TankAnimal> allTankAnimal() {
        List<TankAnimal> list = dslContext
                .selectFrom(ZO_TANK_ANIMAL)
                .fetchInto(TankAnimal.class);

        LOG.debug("allTankAnimal(): count={}", list.size());
        return list;
    }


    private LocalDateTime getCurrDate() {
        return LocalDateTime.now();
    }

}
