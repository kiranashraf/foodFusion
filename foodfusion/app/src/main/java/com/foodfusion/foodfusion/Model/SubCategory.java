package com.foodfusion.foodfusion.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rameez on 3/7/2018.
 */

public class SubCategory {
    @SerializedName("sub_category")
    @Expose
    private String subCategory;
    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("total_recipes")
    @Expose
    private Integer totalRecipes;

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public Integer getTotalRecipes() {
        return totalRecipes;
    }

    public void setTotalRecipes(Integer totalRecipes) {
        this.totalRecipes = totalRecipes;
    }
}
