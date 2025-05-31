package com.example.dao.impl;

import com.example.dao.DataLogDao;
import com.example.db.connection.HibernateSessionFactory;
import com.example.entity.DataLog;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataLogDaoImpl implements DataLogDao {

    private static final String SELECT_ALL = "from DataLog";

    private final HibernateSessionFactory hibernateSessionFactory;

    public DataLogDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public DataLog create(DataLog dataLog) {
        Session session = hibernateSessionFactory.getCurrentSession();
        session.persist(dataLog);
        return dataLog;
    }

    @Override
    public DataLog findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        return session.get(DataLog.class, id);
    }

    @Override
    public void update(DataLog dataLog) {
        Session session = hibernateSessionFactory.getCurrentSession();
        session.merge(dataLog);
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        DataLog dataLog = session.get(DataLog.class, id);

        if (dataLog != null) {
            session.remove(dataLog);
        }
    }

    @Override
    public List<DataLog> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.createQuery(SELECT_ALL, DataLog.class).list();
    }
}