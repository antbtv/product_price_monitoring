package com.example.implementationdao;

import com.example.dao.PriceHistoryDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.PriceHistory;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceHistoryDaoImpl implements PriceHistoryDao {

    private final String SELECT_ALL = "from PriceHistory";

    private final HibernateSessionFactory hibernateSessionFactory;

    public PriceHistoryDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public void create(PriceHistory priceHistory) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.persist(priceHistory);
    }

    @Override
    public PriceHistory findById(int id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        return session.get(PriceHistory.class, id);
    }

    @Override
    public void update(PriceHistory priceHistory) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.merge(priceHistory);
    }

    @Override
    public void delete(int id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        PriceHistory priceHistory = session.get(PriceHistory.class, id);
        if (priceHistory != null) {
            session.remove(priceHistory);
        }
    }

    @Override
    public List<PriceHistory> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();

        return session.createQuery(SELECT_ALL, PriceHistory.class).list();
    }
}
