package com.example.portfoliobackend.dto;

public class MarketNewsDTO {
    private String headline;
    private String source;
    private String url;
    private Long datetime;
    private String summary;
    private String image;

    public MarketNewsDTO() {
    }

    public MarketNewsDTO(String headline, String source, String url, Long datetime, String summary, String image) {
        this.headline = headline;
        this.source = source;
        this.url = url;
        this.datetime = datetime;
        this.summary = summary;
        this.image = image;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
