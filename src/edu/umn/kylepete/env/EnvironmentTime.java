package edu.umn.kylepete.env;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class EnvironmentTime {
	
	@SuppressWarnings("serial")
	public static class EnvironmentTimeException extends Exception {
		public EnvironmentTimeException(String msg) {
			super(msg);
		}
	};
	
	private static Long curTimeInSeconds;
	private static Map<Long, Queue<TimeListener>> listeners = new HashMap<Long, Queue<TimeListener>>();
	
	public static void initializeTime(Date startTime){
		curTimeInSeconds = startTime.getTime() / 1000;
	}

	public static Date getCurTime(){
		return new Date(curTimeInSeconds * 1000);
	}
	
	/**
	 * Get the number of seconds elapsed since the given previousTime
	 * 
	 * @param previousTime
	 * @return
	 */
	public static long getElapsed(Date previousTime) {
		return curTimeInSeconds - previousTime.getTime() / 1000;
	}
	
	public static boolean advanceTime(){
		curTimeInSeconds++;
		return executeListeners();
	}
	
	private static boolean executeListeners(){
	    if (listeners.size() == 0) { // Nothing else to do
	        return false;
	    }
		Queue<TimeListener> queue = listeners.get(curTimeInSeconds);
		if (queue != null) {
			TimeListener listener = queue.poll();
			while (listener != null) {
				excecuteListener(listener);
				listener = queue.poll();
			}
			listeners.remove(curTimeInSeconds);
		}
		return true;
	}
	
	private static void excecuteListener(TimeListener listener) {
		// TODO start a new thread?
		listener.ariveAtTime();
	}

	public static void waitForTime(Date time, TimeListener callback) throws EnvironmentTimeException{
		if (callback == null) {
			throw new EnvironmentTimeException("callback must not be null");
		}
		Long key = new Long(time.getTime() / 1000);
		if (key <= curTimeInSeconds) {
			throw new EnvironmentTimeException("Cannot waitForTime in the past");
		}

		Queue<TimeListener> queue = listeners.get(key);
		if (queue == null) {
			queue = new LinkedList<TimeListener>();
			listeners.put(key, queue);
		}
		queue.add(callback);
	}
}
