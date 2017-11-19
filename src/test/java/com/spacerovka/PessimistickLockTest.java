package com.spacerovka;

import com.spacerovka.domain.Item;
import org.hibernate.*;
import org.hibernate.exception.LockAcquisitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PessimistickLockTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test(expected = LockAcquisitionException.class)
    public void pessimisticWrite() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(new Item("First"));
        transaction.commit();
        session.close();

        Session session2 = sessionFactory.openSession();

        Session session3 = sessionFactory.openSession();
        Transaction transaction2 = session3.beginTransaction();
        Item entity2 = (Item) session3.get(Item.class, 1L);
        entity2.setName("New Name");
        session3.saveOrUpdate(entity2);

        Transaction transaction1 = session2.beginTransaction();
        Item entity1 = (Item) session2.get(Item.class, 1L);
        session2.buildLockRequest(new LockOptions(LockMode.PESSIMISTIC_WRITE)).setTimeOut(1).lock(entity1);

        transaction2.commit();
    }

}
