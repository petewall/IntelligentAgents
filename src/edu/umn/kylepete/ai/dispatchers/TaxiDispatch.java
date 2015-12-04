package edu.umn.kylepete.ai.dispatchers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.umn.kylepete.ai.TaxiAgent;
import edu.umn.kylepete.env.Request;
import edu.umn.kylepete.env.RequestListener;

public abstract class TaxiDispatch implements RequestListener {
	
	protected List<TaxiAgent> waitingTaxis;
	protected List<TaxiAgent> busyTaxis;
	protected Queue<Request> requestQueue;

	public TaxiDispatch(){
		waitingTaxis = new ArrayList<TaxiAgent>();
		busyTaxis = new ArrayList<TaxiAgent>();
		requestQueue = new LinkedList<Request>();
	}

	public void addTaxi(TaxiAgent taxi){
		waitingTaxis.add(taxi);
	}
	
    public abstract void newRequest(Request event);

    public abstract void requestComplete(TaxiAgent taxiAgent);
}
