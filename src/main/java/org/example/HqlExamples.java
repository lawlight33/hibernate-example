package org.example;

import org.example.models.Person;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class HqlExamples {

    static Logger logger = LoggerFactory.getLogger(HqlExamples.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("============ HqlExamples ============");
        new Thread(HqlExamples::startDB).start();

        Configuration configuration = new Configuration().addAnnotatedClass(Person.class);

        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            writeData(sessionFactory);
            readData(sessionFactory);
            logger.info("---------------");
            readSpecificData(sessionFactory);
            logger.info("---------------");
            modifyData(sessionFactory);
            readData(sessionFactory);
            logger.info("---------------");
            deleteData(sessionFactory);
            readData(sessionFactory);
        }

        logger.info("Done!!!");
        Thread.sleep(500000);
    }

    private static void writeData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Person person1 = new Person("name", 12);
        Person person2 = new Person("name", 45);
        session.persist(person1);
        session.persist(person2);
        session.getTransaction().commit();
    }

    private static void readData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        List<Person> result = session
                .createQuery("FROM Person", Person.class)
                .getResultList();
        logger.info("Found size: {}", result.size());
        for (Person p : result) {
            logger.info("Found person: {}", p);
        }
        session.getTransaction().commit();
    }

    private static void readSpecificData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        List<Person> result = session
                .createQuery("FROM Person WHERE age > 13", Person.class)
                .getResultList();
        logger.info("Found size: {}", result.size());
        for (Person p : result) {
            logger.info("Found person: {}", p);
        }
        session.getTransaction().commit();
    }

    private static void modifyData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        int count = session
                .createQuery("UPDATE Person SET age = -1 WHERE age > 13 ")
                .executeUpdate();
        logger.info("Updated rows: {}", count);
        session.getTransaction().commit();
    }

    private static void deleteData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        int count = session
                .createQuery("DELETE FROM Person WHERE age < 0 ")
                .executeUpdate();
        logger.info("Updated rows: {}", count);
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
