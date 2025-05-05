package com.example.implementationdao;

import com.example.MessageSources;
import com.example.dao.StoreDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.Store;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StoreDaoImpl implements StoreDao {

    private static final String SELECT_ALL = "from Store";

    private final HibernateSessionFactory hibernateSessionFactory;
    private static final Logger logger = LogManager.getLogger(StoreDaoImpl.class);

    public StoreDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public Store create(Store store) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.persist(store);
            logger.debug(MessageSources.SUCCESS_CREATE);
            return store;
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_CREATE);
            return null;
        }
    }

    @Override
    public Store findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Store store = null;

        try {
            store = session.get(Store.class, id);
            if (store == null) {
                logger.error(MessageSources.FAILURE_READ_ONE);
            } else {
                logger.debug(MessageSources.SUCCESS_READ_ONE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        }

        return store;
    }

    @Override
    public void update(Store store) {
        Session session = hibernateSessionFactory.getCurrentSession();

        try {
            session.merge(store);
            logger.debug(MessageSources.SUCCESS_UPDATE);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_UPDATE);
        }
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Store store = session.get(Store.class, id);

        try {
            if (store != null) {
                session.remove(store);
                logger.debug(MessageSources.SUCCESS_DELETE);
            } else {
                logger.error(MessageSources.FAILURE_DELETE);
            }
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<Store> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<Store> stores = session.createQuery(SELECT_ALL, Store.class).list();

        try {
            logger.debug(MessageSources.SUCCESS_READ_MANY);
        } catch (Exception e) {
            logger.error(MessageSources.FAILURE_READ_MANY);
        }
        return stores;
    }
}
