package com.foodfusion.foodfusion.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rameez on 2/28/2018.
 */

public class Images {

    @SerializedName("thumbnail0")
    @Expose
    private String thumbnail0;
    @SerializedName("thumbnail1")
    @Expose
    private String thumbnail1;
    @SerializedName("thumbnail2")
    @Expose
    private String thumbnail2;
    @SerializedName("thumbnail3")
    @Expose
    private String thumbnail3;
    @SerializedName("thumbnail4")
    @Expose
    private String thumbnail4;

    public String getThumbnail0() {
        return thumbnail0;
    }

    public void setThumbnail0(String thumbnail0) {
        this.thumbnail0 = thumbnail0;
    }

    public String getThumbnail1() {
        return thumbnail1;
    }

    public void setThumbnail1(String thumbnail1) {
        this.thumbnail1 = thumbnail1;
    }

    public String getThumbnail2() {
        return thumbnail2;
    }

    public void setThumbnail2(String thumbnail2) {
        this.thumbnail2 = thumbnail2;
    }

    public String getThumbnail3() {
        return thumbnail3;
    }

    public void setThumbnail3(String thumbnail3) {
        this.thumbnail3 = thumbnail3;
    }

    public String getThumbnail4() {
        return thumbnail4;
    }

    public void setThumbnail4(String thumbnail4) {
        this.thumbnail4 = thumbnail4;
    }
}