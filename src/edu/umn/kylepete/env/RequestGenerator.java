package edu.umn.kylepete.env;

import java.util.ArrayList;
import java.util.List;

import edu.umn.kylepete.db.TaxiData;
import edu.umn.kylepete.env.EnvironmentTime.EnvironmentTimeException;

public class RequestGenerator {

    private TaxiData db;
    List<RequestListener> requestListeners;

    public RequestGenerator(TaxiData db) {
        this.db = db;
        this.requestListeners = new ArrayList<RequestListener>();
    }
    
    public void addRequestListener(RequestListener listener){
    	this.requestListeners.add(listener);
    }
    
	public void generateRequests() throws EnvironmentTimeException {
    	Request request = db.getNextRequest();
    	// TODO need to limit this with some sort of paging and delay so we don't fill up memory
    	while(request != null){
    		EnvironmentTime.waitForTime(request.getTime(), new RequestCallback(request));
    		request = db.getNextRequest();
    	}
    }

    private void triggerRequest(Request request){
    	//TODO make this multi-threaded?
    	for(RequestListener listener: this.requestListeners){
    		listener.newRequest(request);
    	}
    }
	
	private class RequestCallback implements TimeListener{
		Request request;
		
		public RequestCallback(Request r) {
			this.request = r;
		}

		public void ariveAtTime() {
			triggerRequest(request);
		}
	}
}
