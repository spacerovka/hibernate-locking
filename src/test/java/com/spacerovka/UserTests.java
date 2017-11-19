package com.spacerovka;

import com.spacerovka.mappings.Error;
import com.spacerovka.mappings.Role;
import com.spacerovka.mappings.User;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTests {

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

    @Test
    public void saveUser() {
        User user = new User("marla");
        Error e = new Error();
        e.setUserError("talk about fight club");
        user.getErrors().add(e);
        Role role = new Role();
        role.setName(Role.RoleName.USER);
        user.getRoles().add(role);
        session.persist(user);
        transaction.commit();
        session.close();

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        User entity = (User) session.createQuery("from User").list().get(0);
        assertEquals("talk about fight club", entity.getErrors().iterator().next().getUserError());
        assertTrue(entity.getRoles().iterator().next().getAuthorities().contains(Role.Authority.VIEW));

    }
}
