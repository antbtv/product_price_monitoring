package com.example.implementationdao;

import com.example.dao.security.UserDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.security.User;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private static final String SELECT_ALL = "from User";

    private final HibernateSessionFactory hibernateSessionFactory;

    public UserDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public void create(User user) {
        Session session = hibernateSessionFactory.getCurrentSession();
        session.persist(user);
    }

    @Override
    public User findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public void update(User user) {
        Session session = hibernateSessionFactory.getCurrentSession();
        session.merge(user);
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        User user = session.get(User.class, id);

        if (user != null) {
            session.remove(user);
        }
    }

    @Override
    public List<User> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.createQuery(SELECT_ALL, User.class).list();
    }
}
