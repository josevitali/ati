package ar.edu.itba.ati.events;

public class ReturnEvent {

    private final Object returnValue;

    public Object getReturnValue() {
        return returnValue;
    }

    public ReturnEvent(Object returnValue) {
        this.returnValue = returnValue;
    }
}
