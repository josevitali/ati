package ar.edu.itba.ati.events.alerts;

public class AlertMessageEvent {

    private String message;
    private String title = "Information";

    public AlertMessageEvent(String title, String message) {
        this.message = message;
        this.title = title;

    }

    public AlertMessageEvent(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    public String getTitle() {
        return title;
    }
}
