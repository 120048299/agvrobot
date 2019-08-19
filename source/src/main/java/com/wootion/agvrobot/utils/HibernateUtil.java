//package com.wootion.agvrobot.utils;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//
//public class HibernateUtil {
//
//        public static final SessionFactory sessionFactory;
//      static {
//               try {
//                       sessionFactory = new Configuration().configure()
//                                          .buildSessionFactory();
//                        } catch (Throwable ex) {
//                             throw new ExceptionInInitializerError(ex);
//                        }
//               }
//          public static final ThreadLocal session =
//                                                         new ThreadLocal();
//          public static Session currentSession() throws Exception {
//                       Session s =(Session ) session.get();
//                        if(s == null) {
//                                  s = sessionFactory.openSession();
//                                  session.set(s);
//                            }
//                       return s;
//                }
//          public static void closeSession() throws Exception {
//                         Session s = (Session ) session.get();
//                         if(s != null) {
//                                   s.close();
//                             }
//                         session.set(null);
//                }
//   }