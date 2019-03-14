package com.bupt.service.datasource;

import org.aopalliance.intercept.MethodInvocation;

public interface LBStrategy<T> {

    T elect(MethodInvocation mi);

    void removeTarget(T t);

    void recoverTarget(T t);
    
    void onLoad(T t, int id, long timetook);
    
    boolean doFailover();
}