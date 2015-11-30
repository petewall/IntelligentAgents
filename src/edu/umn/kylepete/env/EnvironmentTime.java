package edu.umn.kylepete.env;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class EnvironmentTime {
	
	private Long curTime;
	private Map<Long, Queue<TimeListener>> listeners;
	
	public EnvironmentTime(Date startTime){
		listeners = new HashMap<Long, Queue<TimeListener>>();
		curTime = startTime.getTime();
	}

	public Date getCurTime(){
		return new Date(curTime);
	}
	
	public void advanceTime(){
		curTime++;
		executeListeners();
	}
	
	private void executeListeners(){
		Queue<TimeListener> queue = listeners.get(curTime);
		if(queue != null){
			TimeListener listener = queue.poll();
			while(listener != null){
				excecuteListener(listener);
				listener = queue.poll();
			}
		}
	}
	
	private void excecuteListener(TimeListener listener) {
		// TODO start a new thread?
		listener.ariveAtTime();
	}

	public void waitForTime(Date time, TimeListener callback){
		Long key = new Long(time.getTime());
		if(key >= curTime && callback != null){
			Queue<TimeListener> queue = listeners.get(key);
			if(queue == null){
				queue = new LinkedList<TimeListener>();
				listeners.put(key, queue);
			}
			queue.add(callback);
		}
	}
}
