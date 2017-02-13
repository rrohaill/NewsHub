package com.rohail.beyondinfinity.news.hub.newshub.models;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by rohail on 2/10/2017.
 */

public class ArticleModel implements Serializable {
    private Articles[] articles;

    private String sortBy;

    private String source;

    private String status;

    public Articles[] getArticles() {
        return articles;
    }

    public void setArticles(Articles[] articles) {
        this.articles = articles;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ArticleModel{" +
                "articles=" + Arrays.toString(articles) +
                ", sortBy='" + sortBy + '\'' +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
