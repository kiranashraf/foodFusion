package com.foodfusion.foodfusion.Model;

public class NavDrawerItem {
	
	private String title;
	private int id;

	private boolean isCounterVisible = false;
	
	public NavDrawerItem(){}

	public NavDrawerItem(String title,int id){
		this.title = title;
		this.id=id;
	}
	
	public NavDrawerItem(String title, boolean isCounterVisible, String count){
		this.title = title;
		this.isCounterVisible = isCounterVisible;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }
	
}
