package green_minds.com.finalproject.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import green_minds.com.finalproject.R;

public class CategoryHelper {

    public static String[] listOfCategories = {
            "Bottles / Cans",
            "Water Fountain",
            "Bike Rack",
            "Coffee shop with discount",
            "Batteries / Electronic Waste",
            "Pins Made"
    };
    public static String[] shortCategories ={
            "Bottles",
            "Water\nFountain",
            "Bike\nRack",
            "Discounts",
            "Batteries",
            "Pins\nMade"
    };

    public static String getPinIdentifier(int i){
        return listOfCategories[i];
    }
    //for the database
    public static String getTypeKey(int i){
        switch (i){
            case 0:
                return "bottlecount";
            case 1:
                return "watercount";
            case 2:
                return "bikecount";
            case 3:
                return "coffeecount";
            case 4:
                return "batterycount";
            case 5:
                return "pincount";
        }
        return "error getting category";
    }

    public static Drawable getIconResource(int i, Context c){
        switch (i){
            case 0:
                return c.getResources().getDrawable(R.drawable.ic_recycling_bin);
            case 1:
                return c.getResources().getDrawable(R.drawable.ic_drop);
            case 2:
                return c.getResources().getDrawable(R.drawable.ic_bicycle);
            case 3:
                return c.getResources().getDrawable(R.drawable.ic_coins);
            case 4:
                return c.getResources().getDrawable(R.drawable.ic_battery);
            case 5:
                return c.getResources().getDrawable(R.drawable.pin);
        }
        return c.getResources().getDrawable(R.drawable.ic_recycling_bin);
    }

    public static Category[] categories = {
            new Category("Bottles / Cans", 0, "I want to save", "bottles"),
            new Category("Water Fountain", 1, "I want to refill", "bottles"),
            new Category("Bike Rack", 2, "I want to bike", "times"),
            new Category("Coffee shop with discount", 3, "I want to save", "coffee cups"),
            new Category("Batteries / Electronic Waste", 4, "I want to recycle", "batteries/misc")
    };
}
