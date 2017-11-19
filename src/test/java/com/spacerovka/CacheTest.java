package com.spacerovka;

import com.spacerovka.cahce.CachableChild;
import com.spacerovka.cahce.CachedItem;
import com.spacerovka.domain.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @Before
    public void before() {
        sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

    }

    @Test
    public void firstLevelCache() {
        sessionFactory.getStatistics().clear();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        Item item = new Item();
        item.setName("good book");
        session.save(item);
        transaction.commit();
        session.close();

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        item = session.load(Item.class, 1L);
        item.getName();
        assertEquals(1, sessionFactory.getStatistics().getEntityFetchCount());

        Item item2 = session.load(Item.class, 1L);
        assertEquals("good book", item2.getName());
        assertEquals(1, sessionFactory.getStatistics().getEntityFetchCount());
        transaction.commit();
        session.close();


    }

    @Test
    public void secondLevelCache() {
        sessionFactory.getStatistics().clear();

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        CachedItem item = new CachedItem();
        item.setName("2nd item");
        item.getItems().add(new CachableChild("inner"));
        session.persist(item);
        //Item was not cached to 2nd level after save, because ID type generation is identity
        //and support for cahche on write with READ_WRITE strategy is only for SEQUENCE id
        //sequence is not supperted by mysql :,(
        transaction.commit();
        session.close();

        Session session2 = sessionFactory.openSession();
        transaction = session2.beginTransaction();
        item = session2.load(CachedItem.class, 1L);
        item.getName();
        transaction.commit();
        session2.close();

        Session session3 = sessionFactory.openSession();
        Transaction transaction2 = session3.beginTransaction();
        CachedItem itemFromChache = session3.load(CachedItem.class, 1L);
        itemFromChache.getName();
        transaction2.commit();
        assertEquals("2nd item", itemFromChache.getName());
        assertEquals("inner", itemFromChache.getItems().get(0).getName());
        //Entity is in second level cache
        System.out.println(sessionFactory.getStatistics().getSecondLevelCacheStatistics(CachedItem.class.getName()));
        System.out.println(sessionFactory.getStatistics().getSecondLevelCacheStatistics(CachedItem.class.getName()).getEntries());
        assertEquals(3, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        //3 is CachedItem its collection(separate region) and collection's 1rst item - CachableChild
    }
}
