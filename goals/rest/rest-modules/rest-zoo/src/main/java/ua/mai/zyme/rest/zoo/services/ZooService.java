package ua.mai.zyme.rest.zoo.services;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.mai.zyme.rest.zoo.models.Animal;

import java.util.List;

import static ua.mai.zyme.db.zo.schema.Tables.ZO_ANIMAL;


@Service
public class ZooService {

    private final Logger LOG = LoggerFactory.getLogger("zoo.ZooService");

    private DSLContext dslContext;

    public ZooService(@Autowired DSLContext dslContext ) { // дефолтный dslContext (для zyme)
        this.dslContext = dslContext;
    }

    @Transactional(readOnly = true)
    public List<Animal> getAnimals() {
        List<Animal> list = dslContext
                .selectFrom(ZO_ANIMAL)
                .fetchInto(Animal.class);

        LOG.debug("getAnimals(): count={}", list.size());
        return list;
    }
}
