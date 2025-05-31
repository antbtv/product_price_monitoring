package com.example.dao.impl;

import com.example.dao.StoreDao;
import com.example.db.connection.HibernateSessionFactory;
import com.example.entity.Store;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StoreDaoImpl implements StoreDao {

    private static final String SELECT_ALL = "from Store";

    private final HibernateSessionFactory hibernateSessionFactory;

    public StoreDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public Store create(Store store) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.persist(store);
        return store;
    }

    @Override
    public Store findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.get(Store.class, id);
    }

    @Override
    public void update(Store store) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.merge(store);
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        Store store = session.get(Store.class, id);

        if (store != null) {
            session.remove(store);
        }
    }

    @Override
    public List<Store> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.createQuery(SELECT_ALL, Store.class).list();
    }
}
