package edu.umn.kylepete.env;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class EnvironmentTime {
	
	private static Long curTime;
	private static Map<Long, Queue<TimeListener>> listeners = new HashMap<Long, Queue<TimeListener>>();
	
	public static void initializeTime(Date startTime){
		curTime = startTime.getTime();
	}

	public static Date getCurTime(){
		return new Date(curTime);
	}
	
	public static boolean advanceTime(){
		curTime++;
		return executeListeners();
	}
	
	private static boolean executeListeners(){
	    if (listeners.size() == 0) { // Nothing else to do
	        return false;
	    }
		Queue<TimeListener> queue = listeners.get(curTime);
		if (queue != null) {
			TimeListener listener = queue.poll();
			while (listener != null) {
				excecuteListener(listener);
				listener = queue.poll();
			}
			listeners.remove(curTime);
		}
		return true;
	}
	
	private static void excecuteListener(TimeListener listener) {
		// TODO start a new thread?
		listener.ariveAtTime();
	}

	public static void waitForTime(Date time, TimeListener callback){
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
