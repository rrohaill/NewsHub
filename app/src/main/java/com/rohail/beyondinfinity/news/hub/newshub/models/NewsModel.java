package com.rohail.beyondinfinity.news.hub.newshub.models;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by rohail on 2/9/2017.
 */

public class NewsModel implements Serializable {

    private String status;

    private Sources[] sources;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Sources[] getSources() {
        return sources;
    }

    public void setSources(Sources[] sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "NewsModel{" +
                "status='" + status + '\'' +
                ", sources=" + Arrays.toString(sources) +
                '}';
    }
}
