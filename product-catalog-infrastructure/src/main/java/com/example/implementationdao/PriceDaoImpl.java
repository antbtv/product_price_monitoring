package com.example.implementationdao;

import com.example.dao.PriceDao;
import com.example.dbconnection.HibernateSessionFactory;
import com.example.entity.Price;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceDaoImpl implements PriceDao {

    private final String SELECT_ALL = "from Price";

    private final HibernateSessionFactory hibernateSessionFactory;

    public PriceDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public void create(Price price) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.persist(price);
    }

    @Override
    public Price findById(int id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        return session.get(Price.class, id);
    }

    @Override
    public void update(Price price) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.merge(price);
    }

    @Override
    public void delete(int id) {
        Session session = hibernateSessionFactory.getCurrentSession();

        Price price = session.get(Price.class, id);
        if (price != null) {
            session.remove(price);
        }
    }

    @Override
    public List<Price> findAll() {
        Session session = hibernateSessionFactory.getCurrentSession();

        return session.createQuery(SELECT_ALL, Price.class).list();
    }
}
