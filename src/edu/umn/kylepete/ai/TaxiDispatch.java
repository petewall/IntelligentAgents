package edu.umn.kylepete.ai;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.umn.kylepete.env.Request;
import edu.umn.kylepete.env.RequestListener;

public class TaxiDispatch implements RequestListener{
	
	private List<TaxiAgent> waitingTaxis;
	private List<TaxiAgent> busyTaxis;
	
	private Queue<Request> requestQueue;
	
	public TaxiDispatch(){
		waitingTaxis = new ArrayList<TaxiAgent>();
		busyTaxis = new ArrayList<TaxiAgent>();
		requestQueue = new LinkedList<>();
	}

	public void addTaxi(TaxiAgent taxi){
		waitingTaxis.add(taxi);
	}
	
	@Override
	public void newRequest(Request event) {
		requestQueue.add(event);
		processRequests();
	}

	public void requestComplete(TaxiAgent taxiAgent) {
		busyTaxis.remove(taxiAgent);
		waitingTaxis.add(taxiAgent);
		processRequests();
	}
	
	private void processRequests(){
		while(waitingTaxis.size() > 0 && requestQueue.size() > 0){
			Request request = requestQueue.poll();
			// TODO find nearest waiting taxi instead of the first
			TaxiAgent agent = waitingTaxis.remove(0);
			busyTaxis.add(agent);
			agent.fulfillRequest(request);
		}
	}
}
