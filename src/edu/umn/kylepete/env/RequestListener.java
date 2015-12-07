package edu.umn.kylepete.env;

import edu.umn.kylepete.ai.agents.TaxiAgent;

public interface RequestListener {
    public void newRequest(Request event);
    public void requestComplete(TaxiAgent taxiAgent, Request completedRequest);
}
