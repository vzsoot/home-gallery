package com.akulogics.gallery.bean;

import java.io.Serializable;

/**
 * Created by zsolt_venczel on 2016.08.16
 */
public class GalleryItem implements Serializable {

    private String src;
    private String thumb;
    private String subHtml;
    private String responsive;
    private String downloadUrl;
    private String poster;
    private String html;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getSubHtml() {
        return subHtml;
    }

    public void setSubHtml(String subHtml) {
        this.subHtml = subHtml;
    }

    public String getResponsive() {
        return responsive;
    }

    public void setResponsive(String responsive) {
        this.responsive = responsive;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

}
