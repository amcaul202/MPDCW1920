package org.amcaul202.gcu.mpd.mpdcw1920;

public class Roadwork {
    private String title;
    private String description;
    private String position;

    public Roadwork() {
        title = "";
        description = "";
        position = "";
    }

    public Roadwork(String atitle, String adescription, String aposition) {
        title = atitle;
        description = adescription;
        position = aposition;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPosition() {return position;}

    public void setTitle(String atitle) {
        title = atitle;
    }

    public void setDescription(String adescription) {
        description = adescription;
    }

    public void setPosition(String aposition){
        position = aposition;
    }

    public String toString() {
        String temp;
        temp = title + " " + description + " " + position;
        return temp;
    }
}

