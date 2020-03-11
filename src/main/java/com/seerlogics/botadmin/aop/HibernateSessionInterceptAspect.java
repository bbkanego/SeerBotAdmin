package com.seerlogics.botadmin.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// http://cyriltech.blogspot.com/2017/03/how-to-enable-hibernate-filter-using.html
@Aspect
public class HibernateSessionInterceptAspect {
    Logger log = LoggerFactory.getLogger(HibernateSessionInterceptAspect.class);

    @AfterReturning(pointcut = "execution(* org.hibernate.SessionBuilder.openSession(..))", returning = "session")
    public void forceFilter(JoinPoint joinPoint, Object session) {
        Session hibernateSession = (Session) session;
        hibernateSession.getSessionFactory().getProperties().get("");

        // hibernateSession.enableFilter("groupACL").setParameter("userId", userId);
    }
}
