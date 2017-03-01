package com.rohail.beyondinfinity.news.hub.newshub.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rohail on 2/9/2017.
 */

public class NewsModel implements Serializable {

    private String status;

    private ArrayList<Sources> sources;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Sources> getSources() {
        return sources;
    }

    public void setSources(ArrayList<Sources> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "NewsModel{" +
                "status='" + status + '\'' +
                ", sources=" + sources +
                '}';
    }
}
