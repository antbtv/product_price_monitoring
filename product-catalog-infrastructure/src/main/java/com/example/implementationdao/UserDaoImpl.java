package com.example.implementationdao;

import com.example.MessageSources;
import com.example.dao.security.UserDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.security.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private static final String SELECT_ALL = "from User";
    private static final String SELECT_BY_USERNAME = "FROM User WHERE username = :username";

    private final HibernateSessionFactory hibernateSessionFactory;
    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    public UserDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public User create(User user) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.persist(user);
            logger.debug(MessageSources.SUCCESS_CREATE);
            return user;
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Override
    public User findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        User user = null;

        try {
            user = session.get(User.class, id);

            if (user == null) {
                logger.error(MessageSources.FAILURE_READ_ONE);
            } else {
                logger.debug(MessageSources.SUCCESS_READ_ONE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        }
        return user;
    }

    @Override
    public void update(User user) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.merge(user);
            logger.debug(MessageSources.SUCCESS_UPDATE);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.debug(MessageSources.SUCCESS_DELETE);
            } else {
                logger.error(MessageSources.FAILURE_DELETE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<User> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<User> users = null;

        try {
            users = session.createQuery(SELECT_ALL, User.class).list();
            logger.debug(MessageSources.SUCCESS_READ_MANY);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) {
        Session session = hibernateSessionFactory.getCurrentSession();
        User user = null;

        try {
            user = session.createQuery(SELECT_BY_USERNAME, User.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (user == null) {
                logger.error(MessageSources.FAILURE_READ_ONE);
            } else {
                logger.debug(MessageSources.SUCCESS_READ_ONE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        }
        return user;
    }
}

