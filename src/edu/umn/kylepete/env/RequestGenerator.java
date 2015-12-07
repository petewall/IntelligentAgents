package edu.umn.kylepete.env;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import edu.umn.kylepete.Logger;
import edu.umn.kylepete.TaxiSystemProperties;
import edu.umn.kylepete.db.PostgreSQLTaxiData;
import edu.umn.kylepete.db.TaxiData;
import edu.umn.kylepete.env.EnvironmentTime.EnvironmentTimeException;
import edu.umn.kylepete.stats.RequestStats;

public class RequestGenerator {

	private Date maxRequestTime;
    private TaxiData db;
    List<RequestListener> requestListeners;
	private Date processingTime;

	public RequestGenerator(TaxiSystemProperties properties) throws ParseException {
		this.db = new PostgreSQLTaxiData(properties);
		this.maxRequestTime = properties.getRequestMaxTime();
        this.requestListeners = new ArrayList<RequestListener>();
		this.processingTime = properties.getTimeStart();
    }

	// TODO is this thread safe? should it be synchronized?
	public Date getProcessingTime() {
		return this.processingTime;
	}
    
	public void addRequestListeners(Collection<? extends RequestListener> listeners) {
		for (RequestListener listener : listeners) {
			addRequestListener(listener);
		}
	}

    public void addRequestListener(RequestListener listener){
    	this.requestListeners.add(listener);
    }
    
	public void start(final EnvironmentTime time) {
		Logger.info("ENVIRONMENT", "Starting to generate taxi trip requests in a background thread");
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			public void run() {
				try {
					generateRequests(time);
				} catch (EnvironmentTimeException e) {
					Logger.error("ENVIRONMENT", "Background thread failed to generate requests: " + e.getMessage());
					Logger.error("ENVIRONMENT", Logger.stackTraceToString(e));
				}
			}
		});
	}

	public void generateRequests(EnvironmentTime time) throws EnvironmentTimeException {
		int count = 0;
    	Request request = db.getNextRequest();
    	// TODO need to limit this with some sort of paging and delay so we don't fill up memory
		while (request != null && request.getSubmitTime().compareTo(maxRequestTime) <= 0) {
			count++;
			time.waitForTime(request.getSubmitTime(), new RequestCallback(request));
			this.processingTime = request.getSubmitTime();
    		request = db.getNextRequest();
    	}
		this.processingTime = null;
		Logger.info("ENVIRONMENT", "Done generating " + count + " trip requests");
    }

    private void triggerRequest(Request request){
    	//TODO make this multi-threaded?
        RequestStats.requestSubmitted();
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
