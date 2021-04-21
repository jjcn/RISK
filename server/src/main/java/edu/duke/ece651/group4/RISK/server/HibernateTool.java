package edu.duke.ece651.group4.RISK.server;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.Session.*;
public class HibernateTool {
    private static SessionFactory sessionFactory = buildSessionFactory();
    public HibernateTool() {

    }
    private static SessionFactory buildSessionFactory() {
        try {
            if (sessionFactory == null) {
                Configuration configuration = new Configuration()
                        .configure(HibernateTool.class.getResource("/hibernate.cfg.xml"));
                // TODO: manually add entity classes
                configuration.addAnnotatedClass(UserInfo.class);
                configuration.addAnnotatedClass(Game.class);
                StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
                serviceRegistryBuilder.applySettings(configuration.getProperties());
                ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            }
            return sessionFactory;
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void addUserInfo(UserInfo userInfo) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(userInfo);
            tx.commit();
            tx = session.beginTransaction();
            session.update(userInfo);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static List<UserInfo> getUserInfoList() {
        Session session = sessionFactory.openSession();
        List<UserInfo> usersInfo = session.createQuery("SELECT u FROM UserInfo u", UserInfo.class).getResultList();
        session.close();
        return usersInfo;
    }

    public static void addGameInfo(){

    }

    public static List<GameInfo> getGameInfoList(){
        return null;
    }
}
