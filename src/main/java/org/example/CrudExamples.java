package org.example;

import org.example.models.Person;
import org.h2.tools.Server;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class CrudExamples {

    static Logger logger = LoggerFactory.getLogger(CrudExamples.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("============ CrudExamples ============");
        new Thread(CrudExamples::startDB).start();

        Configuration configuration = new Configuration().addAnnotatedClass(Person.class);

        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            logger.info("---------------");
            writeData(sessionFactory);
            readData(sessionFactory);
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
        Person person1;
        Person person2;

        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        person1 = new Person( "23", 34);
        session.persist(person1);
        person2 = new Person( "24", 35);
        session.persist(person2);
        session.getTransaction().commit();

        logger.info("Persons id is: {}, {}", person1.getId(), person2.getId());
    }

    private static void readData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Person p = session.get(Person.class, 1);
        logger.info("Person: {}", p);
        p = session.get(Person.class, 2);
        logger.info("Person: {}", p);
        session.getTransaction().commit();
    }

    private static void modifyData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Person p = session.get(Person.class, 2);
        p.setAge(303);
        session.getTransaction().commit();
    }

    private static void deleteData(SessionFactory sessionFactory) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        Person p = session.get(Person.class, 1);
        session.remove(p);
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
