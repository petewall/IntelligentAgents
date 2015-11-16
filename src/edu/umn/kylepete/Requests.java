package edu.umn.kylepete;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Requests {
    private Set<Request> activeRequests;
    private Map<Vehicle, Request> assignedRequests;
    
    public Requests() {
        activeRequests = new HashSet<Request>();
        assignedRequests = new HashMap<Vehicle, Request>();
    }
}
