package ar.edu.itba.ati.events.pictures;

import ar.edu.itba.ati.model.pictures.Picture;

public class ShowPictureEvent {

    private Picture picture;

    public ShowPictureEvent(){
        picture = null;
    }

    public ShowPictureEvent(Picture picture){
        this.picture = picture;
    }

    public Picture getPicture() {
        return picture;
    }
}
