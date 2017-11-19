package com.spacerovka;

import com.spacerovka.mappings.User;
import com.spacerovka.mappings.UserDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MappingIntegrationTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @Before
    public void init() {
        sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @Test(expected = ConstraintViolationException.class)
    public void oneToOneIsUnique() {
        UserDetails userDetails = new UserDetails();
        userDetails.setAge(20);

        User user = new User("tyler");
        user.setDetails(userDetails);
        userDetails.setUser(user);
        session.persist(user);
        transaction.commit();

        transaction = session.beginTransaction();
        UserDetails newUserDetails = new UserDetails();
        newUserDetails.setUser(user);
        session.persist(newUserDetails);
        transaction.commit();
    }
}
