package ar.edu.itba.ati.events.returnValues;

public class ReturnEndsEvent {

    private final int[] returnValue;

    public ReturnEndsEvent(int[] returnValue) {
        this.returnValue = returnValue;
    }

    public int[] getReturnValue() {
        return returnValue;
    }
}
