package com.example.dao.impl;

import com.example.dao.PriceHistoryDao;
import com.example.db.connection.HibernateSessionFactory;
import com.example.entity.PriceHistory;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PriceHistoryDaoImpl implements PriceHistoryDao {

    private static final String SELECT_ALL = "from PriceHistory";
    private static final String SELECT_PRICES_BY_PRODUCT_IN_DATA_RANGE = "FROM PriceHistory ph " +
            "WHERE ph.product.id = :productId AND ph.store.storeId = :storeId " +
            "AND ph.recordedAt BETWEEN :startDate AND :endDate";

    private final HibernateSessionFactory hibernateSessionFactory;

    public PriceHistoryDaoImpl(HibernateSessionFactory hibernateSessionFactory) {
        this.hibernateSessionFactory = hibernateSessionFactory;
    }

    @Override
    public PriceHistory create(PriceHistory priceHistory) {
        Session session = hibernateSessionFactory.getCurrentSession();

        session.persist(priceHistory);
        return priceHistory;
    }

    @Override
    public PriceHistory findById(Long id) {
        Session session = hibernateSessionFactory.getCurrentSession();
        return session.get(PriceHistory.class, id);
    }

    @Override
    public void update(PriceHistory priceHistory) {
        Session session = hibernateSessionFactory.getCurrentSession();
        session.merge(priceHistory);
    }

    @Override
    public void delete(Long id) {
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

    @Override
    public List<PriceHistory> findPriceHistoryByProductAndDateRange(Long productId,
                                                                    Long storeId,
                                                                    LocalDate startDate,
                                                                    LocalDate endDate) {
        Session session = hibernateSessionFactory.getCurrentSession();

        return session.createQuery(SELECT_PRICES_BY_PRODUCT_IN_DATA_RANGE, PriceHistory.class)
                .setParameter("productId", productId)
                .setParameter("storeId", storeId)
                .setParameter("startDate", startDate.atStartOfDay())
                .setParameter("endDate", endDate.atTime(23, 59, 59))
                .list();
    }
}
