package com.spacerovka;

import com.spacerovka.domain.DirtyOptimistic;
import com.spacerovka.domain.Item;
import com.spacerovka.domain.OptimisticWithCollection;
import com.spacerovka.domain.OptimisticWithVersion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleStateException;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocksTest {

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

    //In JPA Hibernate native StaleStateException is wrapped in OptimisticLockException
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

    @Test(expected = javax.persistence.OptimisticLockException.class)
    public void collectionOptimisticLockWithJPA() {
        EntityManager manager = entityManagerFactory.createEntityManager();

        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        manager.persist(new OptimisticWithCollection("First collection"));
        transaction.commit();


        EntityTransaction transaction1 = manager.getTransaction();
        transaction1.begin();
        OptimisticWithCollection collection = (OptimisticWithCollection) manager.find(OptimisticWithCollection.class, 1L);

        EntityManager manager2 = entityManagerFactory.createEntityManager();
        EntityTransaction transaction2 = manager2.getTransaction();
        transaction2.begin();
        OptimisticWithCollection otherCollection = (OptimisticWithCollection) manager2.find(OptimisticWithCollection.class, 1L);
        Item item = new Item("First item");
        otherCollection.addItem(item);
        manager2.persist(otherCollection);
        transaction2.commit();

        collection.setName("Final name");
        manager2.merge(collection);
        manager2.persist(collection);
        transaction1.commit();
    }


    @Test
    public void noLockForDistinctValues() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(new DirtyOptimistic("A", "B"));
        transaction.commit();

        Transaction transaction1 = session.beginTransaction();
        DirtyOptimistic entity1 = (DirtyOptimistic) session.get(DirtyOptimistic.class, 1L);

        Session session2 = sessionFactory.openSession();
        Transaction transaction2 = session2.beginTransaction();
        DirtyOptimistic entity2 = (DirtyOptimistic) session2.get(DirtyOptimistic.class, 1L);
        entity2.setValue("aa");
        entityManagerFactory.createEntityManager().merge(entity2);
        transaction2.commit();


        entity1.setOtherValue("bb");
        entityManagerFactory.createEntityManager().merge(entity1);
        transaction1.commit();
        session.close();
        session2.close();

        Session session3 = sessionFactory.openSession();
        transaction = session3.beginTransaction();
        DirtyOptimistic edited = session3.get(DirtyOptimistic.class, 1L);
        assertEquals("aa", edited.getValue());
        assertEquals("bb", edited.getOtherValue());
        transaction.commit();

    }

}
