package models;

import views.Custom.ModViewSelected;

/**
 * Created by yossibarel on 20/04/16.
 */
public class ModItem {
    public int mIndex;
    public double mStart = 0.0;
    public double mEnd = 100.0;
    public ModViewSelected viewHolder;

    public ModItem(String name) {

    }

    public String mName;
    public boolean isSelected;

    public ModItem(String name, int index) {
        mName = name;
        isSelected = false;
        mIndex=index;
    }
}
