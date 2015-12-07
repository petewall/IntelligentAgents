package edu.umn.kylepete.env;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import edu.umn.kylepete.Logger;

public class EnvironmentTime {
	
	@SuppressWarnings("serial")
	public static class EnvironmentTimeException extends Exception {
		public EnvironmentTimeException(String msg) {
			super(msg);
		}
	};
	
	private Long curTimeInSeconds;
	private Map<Long, Queue<TimeListener>> listeners = new HashMap<Long, Queue<TimeListener>>();
	
	public void initializeTime(Date startTime) {
		curTimeInSeconds = startTime.getTime() / 1000;
	}

	public Date getCurTime() {
		return new Date(curTimeInSeconds * 1000);
	}
	
	/**
	 * Get the number of seconds elapsed since the given previousTime
	 * 
	 * @param previousTime
	 * @return
	 */
	public long getElapsed(Date previousTime) {
		return curTimeInSeconds - previousTime.getTime() / 1000;
	}
	
	public Date getNextTime() {
		return new Date((curTimeInSeconds + 1) * 1000);
	}

	public boolean advanceTime() {
		curTimeInSeconds++;
		return executeListeners();
	}
	
	private boolean executeListeners() {
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
	
	private void excecuteListener(TimeListener listener) {
		// TODO start a new thread?
		listener.ariveAtTime();
	}

	public void waitForTime(Date time, TimeListener callback) throws EnvironmentTimeException {
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

    public void cancelTime(Date time, TimeListener callback) {
        Long key = new Long(time.getTime() / 1000);
        if (listeners.containsKey(key)) {
            listeners.get(key).remove(callback);
        } else {
            Logger.warning("TIME", "Asked to cancel something that wasn't there.");
        }
    }
}
