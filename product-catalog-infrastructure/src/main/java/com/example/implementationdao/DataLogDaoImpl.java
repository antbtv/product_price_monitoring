package com.example.implementationdao;

import com.example.MessageSources;
import com.example.dao.DataLogDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.DataLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataLogDaoImpl implements DataLogDao {

    private static final String SELECT_ALL = "from DataLog";

    private final HibernateSessionFactory hibernateSessionFactory;
    private static final Logger logger = LogManager.getLogger(DataLogDaoImpl.class);

    public DataLogDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public DataLog create(DataLog dataLog) {
        Session session = hibernateSessionFactory.getCurrentSession();
        session.persist(dataLog);
        logger.info(MessageSources.SUCCESS_CREATE);
        return dataLog;
    }

    @Override
    public DataLog findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        DataLog dataLog = session.get(DataLog.class, id);

        if (dataLog == null) {
            logger.error(MessageSources.FAILURE_READ_ONE);
        } else {
            logger.info(MessageSources.SUCCESS_READ_ONE);
        }

        return dataLog;
    }

    @Override
    public void update(DataLog dataLog) {
        Session session = hibernateSessionFactory.getCurrentSession();
        session.merge(dataLog);
        logger.info(MessageSources.SUCCESS_UPDATE);
    }

    @Override
    public void delete(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        DataLog dataLog = session.get(DataLog.class, id);

        if (dataLog != null) {
            session.remove(dataLog);
            logger.info(MessageSources.SUCCESS_DELETE);
        } else {
            logger.error(MessageSources.FAILURE_DELETE);
        }
    }

    @Override
    public List<DataLog> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();
        List<DataLog> logs = session.createQuery(SELECT_ALL, DataLog.class).list();
        logger.info(MessageSources.SUCCESS_READ_MANY);
        return logs;
    }
}