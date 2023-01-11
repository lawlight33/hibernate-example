package org.example;

import org.example.models.Order;
import org.example.models.Person;
import org.example.models.Supplier;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class RelationsExamples {

    static Logger logger = LoggerFactory.getLogger(RelationsExamples.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("============ RelationsExamples ============");
        new Thread(RelationsExamples::startDB).start();

        Configuration configuration = new Configuration()
                .addAnnotatedClass(Supplier.class)
                .addAnnotatedClass(Order.class);

        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            writeData1(sessionFactory);
            readData(sessionFactory);
            logger.info("---------------");
            writeData2(sessionFactory);
            readData(sessionFactory);
            logger.info("---------------");
            writeData3(sessionFactory);
            readData(sessionFactory);
            logger.info("---------------");
            removeData(sessionFactory);
            readData(sessionFactory);
            logger.info("---------------");
            modifyData(sessionFactory);
            readData(sessionFactory);
        }

        logger.info("Done!!!");
        Thread.sleep(500000);
    }

    private static void writeData1(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Supplier supplier1 = new Supplier("name1");
        Supplier supplier2 = new Supplier("name2");
        session.persist(supplier1);
        session.persist(supplier2);
        Order order1 = new Order("orName1", supplier1);
        Order order2 = new Order("orName2", supplier1);
        Order order3 = new Order("orName33", supplier2);
        session.persist(order1);
        session.persist(order2);
        session.persist(order3);
        session.getTransaction().commit();
    }

    private static void readData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Supplier supplier1 = session.get(Supplier.class, 1);
        Supplier supplier2 = session.get(Supplier.class, 2);
        logger.info("Supplier: {}", supplier1);
        logger.info("Supplier: {}", supplier2);
        session.getTransaction().commit();
    }

    private static void writeData2(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Supplier supplier1 = session.get(Supplier.class, 1);
        Order order5 = new Order("orName555", supplier1);
        // Manual addition because of caching
        supplier1.getOrders().add(order5);
        session.persist(order5);
        session.getTransaction().commit();
    }

    private static void writeData3(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Supplier supplier = new Supplier("name2222");
        Order order = new Order("orName777", supplier);
        // Must save each instance
        session.persist(supplier);
        session.persist(order);
        session.getTransaction().commit();
    }

    private static void removeData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Supplier supplier = session.get(Supplier.class, 3);
        session.remove(supplier);
        session.getTransaction().commit();
    }

    private static void modifyData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Order order = session.get(Order.class, 1);
        Supplier supplier = session.get(Supplier.class, 2);
        order.setSupplier(supplier);
        // Manual manipulations because of caching
        order.getSupplier().getOrders().remove(order);
        supplier.getOrders().add(order);
        session.getTransaction().commit();
    }

    private static void startDB() {
        try {
            logger.info("Starting DB ...");
            int port = 9092;
            Server.createTcpServer("-tcpPort", String.valueOf(port), "-tcpAllowOthers", "-ifNotExists").start();
            logger.info("DB was started successfully in localhost:{}", port);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void stopDB() throws SQLException {
        Server.shutdownTcpServer("tcp://localhost:9092", "", true, true);
    }
}
