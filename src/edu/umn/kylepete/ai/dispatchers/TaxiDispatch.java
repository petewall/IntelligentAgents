package edu.umn.kylepete.ai.dispatchers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.umn.kylepete.ai.agents.TaxiAgent;
import edu.umn.kylepete.env.Request;
import edu.umn.kylepete.env.RequestListener;

public abstract class TaxiDispatch implements RequestListener {
	
	protected Queue<Request> requestQueue;

	public TaxiDispatch(){
		requestQueue = new LinkedList<Request>();
	}

	public abstract void addTaxi(TaxiAgent taxi);
	
    public abstract void newRequest(Request event);

    public abstract void requestComplete(TaxiAgent taxiAgent);
}
