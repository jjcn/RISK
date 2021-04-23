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
    protected static SessionFactory sessionFactory = buildSessionFactory("/hibernate.cfg.xml");
    protected static boolean isStartTest = false;
    public HibernateTool() {

    }

    protected static SessionFactory buildSessionFactory(String configureFile) {
        try {

            Configuration configuration = new Configuration()
                    .configure(HibernateTool.class.getResource(configureFile));
            // TODO: manually add entity classes
            configuration.addAnnotatedClass(UserInfo.class);
            configuration.addAnnotatedClass(GameInfo.class);
            StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
            serviceRegistryBuilder.applySettings(configuration.getProperties());
            ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//                sessionFactory = sessionFactoryTemp;
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
        } catch (Throwable e) {
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



    public static void addGameInfo(GameInfo gInfo){
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(gInfo);
            tx.commit();
        } catch (Throwable e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    synchronized public static void updateGameInfo(GameInfo GameInfo) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(GameInfo);
            tx.commit();
        } catch (Throwable e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    public static List<GameInfo> getGameInfoList(){
        Session session = sessionFactory.openSession();
        List<GameInfo> gamesInfo = session.createQuery("SELECT u FROM GameInfo u", GameInfo.class).getResultList();
        session.close();
        return gamesInfo;
    }

    public static void deleteGameInfo(GameInfo gInfo){
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.delete(gInfo);
            tx.commit();
        } catch (Throwable e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }



    public static void deleteUserInfo(UserInfo uInfo){
        Session session = sessionFactory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.delete(uInfo);
            tx.commit();
        } catch (Throwable e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
