package com.foodfusion.foodfusion.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rameez on 2/28/2018.
 */

public class RecipeDetailModel {
    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("full_recipe_detail_roman")
    @Expose
    private String fullRecipeDetailRoman;
    @SerializedName("full_recipe_detail_english")
    @Expose
    private String fullRecipeDetailEnglish;
    @SerializedName("total_views")
    @Expose
    private String totalViews;
    @SerializedName("total_likes")
    @Expose
    private String totalLikes;
    @SerializedName("is_favourite")
    @Expose
    private Integer isFavourite;
    @SerializedName("sponsor_image")
    @Expose
    private String sponsorImage;
    @SerializedName("sponsor_name")
    @Expose
    private String sponsorName;
    @SerializedName("recipe_by")
    @Expose
    private String recipeBy;
    @SerializedName("images")
    @Expose
    private Images images;

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getFullRecipeDetailRoman() {
        return fullRecipeDetailRoman;
    }

    public void setFullRecipeDetailRoman(String fullRecipeDetailRoman) {
        this.fullRecipeDetailRoman = fullRecipeDetailRoman;
    }

    public String getFullRecipeDetailEnglish() {
        return fullRecipeDetailEnglish;
    }

    public void setFullRecipeDetailEnglish(String fullRecipeDetailEnglish) {
        this.fullRecipeDetailEnglish = fullRecipeDetailEnglish;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Integer isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getSponsorImage() {
        return sponsorImage;
    }

    public void setSponsorImage(String sponsorImage) {
        this.sponsorImage = sponsorImage;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getRecipeBy() {
        return recipeBy;
    }

    public void setRecipeBy(String recipeBy) {
        this.recipeBy = recipeBy;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }
}

