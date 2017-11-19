package com.spacerovka;

import com.spacerovka.mappings.AllColumnsEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MappingTypesTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private SessionFactory sessionFactory;
    private Session session;

    @Test
    public void createEntity() {
        sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        AllColumnsEntity allColumnsEntity = new AllColumnsEntity();
        allColumnsEntity.setLongDescription(getLongString());
        allColumnsEntity.setMoney(new BigDecimal(200.678));
        allColumnsEntity.setTopic(AllColumnsEntity.Topic.JPA);
        session.persist(allColumnsEntity);
        transaction.commit();
        session.close();
        //open new session to load entity from db, not from cache
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        AllColumnsEntity entity = session.get(AllColumnsEntity.class, 1L);
        assertFalse(entity.isShared());
        assertTrue(entity.getActive());
        assertEquals(new BigDecimal("200.68"), entity.getMoney());
        assertEquals(AllColumnsEntity.Topic.JPA, entity.getTopic());
    }

    private String getLongString() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ultricies blandit lorem, non blandit mi. Mauris porttitor lectus sit amet felis tempus, sit amet bibendum dui dapibus. Maecenas augue tellus, porttitor a odio eget, venenatis dictum sapien. Nam gravida arcu quis lectus elementum efficitur. Sed vitae augue vitae eros eleifend vehicula. Integer et posuere est. Vestibulum fringilla neque quis eros sagittis laoreet. Nunc faucibus vestibulum lacus vel congue. Etiam sit amet purus id sem suscipit rhoncus id accumsan justo. Morbi in suscipit urna. Curabitur nisi lacus, euismod id hendrerit sit amet, sodales et risus. Cras scelerisque vulputate purus, tristique laoreet tortor pretium vel. Integer porta, magna sit amet lacinia blandit, velit ante maximus lectus, eu ornare leo ipsum varius leo. Donec gravida, tellus eu iaculis consectetur, nunc nisi tempus justo, id venenatis dolor sapien sagittis erat. Aenean sed magna purus. In in sem aliquam, dapibus tellus id, pretium nullam.";
    }
}
