package edu.umn.kylepete;

public class RequestEvent {
    private Request request;
    private long requestTime;

    public RequestEvent(Request request, long requestTime) {
        this.request = request;
        this.requestTime = requestTime;
    }

    public Request getRequest() {
        return request;
    }

    public long getRequestTime() {
        return requestTime;
    }
}
