package com.foodfusion.foodfusion.Model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rameez on 3/7/2018.
 */

public class CategoryModel implements  Comparable<CategoryModel>{
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("total_recipes")
    @Expose
    private Integer totalRecipes;
    @SerializedName("sub_category")
    @Expose
    private List<SubCategory> subCategory = null;

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTotalRecipes() {
        return totalRecipes;
    }

    public void setTotalRecipes(Integer totalRecipes) {
        this.totalRecipes = totalRecipes;
    }

    public List<SubCategory> getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(List<SubCategory> subCategory) {
        this.subCategory = subCategory;
    }

    @Override
    public int compareTo(@NonNull CategoryModel categoryModel) {
        if (categoryModel.getCategory().length() < getCategory().length()) {
            return 1;
        }
        else if (categoryModel.getCategory().length() >  getCategory().length()) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
