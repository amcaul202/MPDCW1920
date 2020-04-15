package org.amcaul202.gcu.mpd.mpdcw1920;

public class Roadwork {
    private String title;
    private String description;

    public Roadwork() {
        title = "";
        description = "";
    }

    public Roadwork(String atitle, String adescription) {
        title = atitle;
        description = adescription;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String atitle) {
        title = atitle;
    }

    public void setDescription(String adescription) {
        description = adescription;
    }

    public String toString() {
        String temp;
        temp = title + " " + description;
        return temp;
    }
}

