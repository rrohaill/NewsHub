package com.rohail.beyondinfinity.news.hub.newshub.models;

import java.util.Arrays;

/**
 * Created by rohail on 2/9/2017.
 */

public class Sources {

    private String id;

    private String category;

    private UrlsToLogos urlsToLogos;

    private String description;

    private String[] sortBysAvailable;

    private String name;

    private String language;

    private String url;

    private String country;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public UrlsToLogos getUrlsToLogos() {
        return urlsToLogos;
    }

    public void setUrlsToLogos(UrlsToLogos urlsToLogos) {
        this.urlsToLogos = urlsToLogos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getSortBysAvailable() {
        return sortBysAvailable;
    }

    public void setSortBysAvailable(String[] sortBysAvailable) {
        this.sortBysAvailable = sortBysAvailable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "SourcesModel{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", urlsToLogos=" + urlsToLogos +
                ", description='" + description + '\'' +
                ", sortBysAvailable=" + Arrays.toString(sortBysAvailable) +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", url='" + url + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
