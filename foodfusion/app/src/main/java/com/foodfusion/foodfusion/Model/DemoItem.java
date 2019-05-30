package com.foodfusion.foodfusion.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

/**
 * Created by Rameez on 3/7/2018.
 */

public class DemoItem implements AsymmetricItem {
    private int columnSpan;
    private int rowSpan;
    private int position;
    private RecipeModel recipe;
    public DemoItem() {
        this(1, 1, 0,null);
    }

    public DemoItem(int columnSpan, int rowSpan, int position,RecipeModel recipe) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
        this.recipe=recipe;
    }

    public DemoItem(Parcel in) {
        readFromParcel(in);
    }

    @Override public int getColumnSpan() {
        return columnSpan;
    }

    @Override public int getRowSpan() {
        return rowSpan;
    }

    public RecipeModel getRecipe(){
        return recipe;
    }

    public int getPosition() {
        return position;
    }

    @Override public String toString() {
        return String.format("%s: %sx%s", position, rowSpan, columnSpan);
    }

    @Override public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();
    }

    @Override public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);
    }

    /* Parcelable interface implementation */
    public static final Parcelable.Creator<DemoItem> CREATOR = new Parcelable.Creator<DemoItem>() {

        @Override public DemoItem createFromParcel(@NonNull Parcel in) {
            return new DemoItem(in);
        }

        @Override @NonNull
        public DemoItem[] newArray(int size) {
            return new DemoItem[size];
        }
    };
}