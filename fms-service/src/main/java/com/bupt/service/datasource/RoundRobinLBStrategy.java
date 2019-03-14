package com.bupt.service.datasource;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoundRobinLBStrategy implements LBStrategy<String> {
    
    private static final Logger LOGGER = Logger.getLogger(RoundRobinLBStrategy.class);
    
    private static final int MIN_LB_FACTOR = 1;
    
    private List<String> targets;
    private int currentPos;
    
    private Map<String, Integer> currentTargets;
    private Map<String, Integer> failedTargets;
    
    public RoundRobinLBStrategy(Map<String, Integer> lbFactors) {
        currentTargets = Collections.synchronizedMap(lbFactors);
        failedTargets = Collections
                .synchronizedMap(new HashMap<String, Integer>(currentTargets.size()));
        reInitTargets(currentTargets);
    }
    
    private void reInitTargets(Map<String, Integer> lbFactors) {
        targets = initTargets(lbFactors);
        currentPos = 0;
        if (targets == null) {
            throw new RuntimeException("target datasources can not be empty");
        }
    }
    
    public synchronized String elect(MethodInvocation mi) {
        if (targets == null) {
            return null;
        }
        if (currentPos >= targets.size()) {
            currentPos = 0;
        }
        return targets.get(currentPos++);
    }
    
    protected synchronized List<String> getTargets() {
        if (targets == null) {
            targets = new ArrayList<String>();
        }
        return targets;
    }
    
    public List<String> initTargets(Map<String, Integer> lbFactors) {
        if (lbFactors == null || lbFactors.size() == 0) {
            return null;
        }
        fixFactor(lbFactors);
        
        Collection<Integer> factors = lbFactors.values();
        // get min factor
        int min = Collections.min(factors);
        if (min > MIN_LB_FACTOR && canModAll(min, factors)) {
            return buildBalanceTargets(lbFactors, min);
        }
        
        return buildBalanceTargets(lbFactors, MIN_LB_FACTOR);
    }
    
    private void fixFactor(Map<String, Integer> lbFactors) {
        Set<Map.Entry<String, Integer>> setEntries = lbFactors.entrySet();
        for (Map.Entry<String, Integer> entry : setEntries) {
            if (entry.getValue() < MIN_LB_FACTOR) {
                entry.setValue(MIN_LB_FACTOR);
            }
        }
    }
    
    private boolean canModAll(int base, Collection<Integer> factors) {
        for (Integer integer : factors) {
            if (integer % base != 0) {
                return false;
            }
        }
        return true;
    }
    
    private List<String> buildBalanceTargets(Map<String, Integer> lbFactors, int baseFactor) {
        Set<Map.Entry<String, Integer>> setEntries = lbFactors.entrySet();
        int factor;
        List<String> targets = new ArrayList<String>(100);
        for (Map.Entry<String, Integer> entry : setEntries) {
            factor = entry.getValue() / baseFactor;
            
            for (int i = 0; i < factor; i++) {
                targets.add(entry.getKey());
            }
        }
        return targets;
    }
    
    public synchronized void removeTarget(String key) {
        if (currentTargets.containsKey(key)) {
            failedTargets.put(key, currentTargets.get(key));
            currentTargets.remove(key);
            try {
                reInitTargets(currentTargets);
            } catch (Exception e) {
                LOGGER.error("{}", e);
            }
        }
    }
    
    public synchronized void recoverTarget(String key) {
        if (failedTargets.containsKey(key)) {
            currentTargets.put(key, failedTargets.get(key));
            failedTargets.remove(key);
            try {
                reInitTargets(currentTargets);
            } catch (Exception e) {
                LOGGER.error("{}", e);
            }
        }
    }
    
    public void onLoad(String t, int id, long timetook) {
    }
    
    public boolean doFailover() {
        return true;
    }
}
