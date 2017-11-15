package com.spacerovka;

import com.spacerovka.domain.Item;
import com.spacerovka.domain.OptimisticWithCollection;
import com.spacerovka.domain.OptimisticWithVersion;
import org.hibernate.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HibernateApplicationTests {

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Test(expected = StaleStateException.class)
    public void optimisticLockWithVersion() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(new OptimisticWithVersion("First name"));
        transaction.commit();

        Transaction transaction1 = session.beginTransaction();
        OptimisticWithVersion entity1 = (OptimisticWithVersion) session.get(OptimisticWithVersion.class, 1L);

        Session session2 = sessionFactory.openSession();
        Transaction transaction2 = session2.beginTransaction();
        OptimisticWithVersion entity2 = (OptimisticWithVersion) session2.get(OptimisticWithVersion.class, 1L);
        entity2.setName("New Name");
        session2.saveOrUpdate(entity2);
        transaction2.commit();


        entity1.setName("Last Name");
        session.saveOrUpdate(entity1);
        transaction1.commit();

        session.close();
        session2.close();
    }

    @Test(expected = StaleStateException.class)
    public void collectionOptimisticLock() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(new OptimisticWithCollection("First collection"));
        transaction.commit();


        Transaction transaction1 = session.beginTransaction();
        OptimisticWithCollection collection = (OptimisticWithCollection) session.get(OptimisticWithCollection.class, 1L);

        Session session2 = sessionFactory.openSession();
        Transaction transaction2 = session2.beginTransaction();

        OptimisticWithCollection otherCollection = (OptimisticWithCollection) session2.get(OptimisticWithCollection.class, 1L);
        Item item = new Item("First item");
        otherCollection.addItem(item);
        session2.save(otherCollection);
        transaction2.commit();

        collection.setName("Final name");
        session.persist(collection);
        transaction1.commit();
    }

    @Test(expected = PessimisticLockException.class)
    public void pessimisticWrite() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(new Item("First"));
        transaction.commit();

        Transaction transaction1 = session.beginTransaction();
        Item entity1 = (Item) session.get(Item.class, 1L, new LockOptions(LockMode.PESSIMISTIC_WRITE));

        Session session2 = sessionFactory.openSession();
        Transaction transaction2 = session2.beginTransaction();
        Item entity2 = (Item) session2.get(Item.class, 1L);
        entity2.setName("New Name");
        session2.saveOrUpdate(entity2);
        transaction2.commit();
    }

}
