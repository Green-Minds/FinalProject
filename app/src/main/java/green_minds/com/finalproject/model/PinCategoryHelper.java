package green_minds.com.finalproject.model;

public class PinCategoryHelper {

    public static String[] listOfCategories = {
            "Bottles / Cans",
            "Water Fountain",
            "Bike Rack",
            "Coffee shop with discount",
            "Batteries / Electronic Waste"
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
        }
        return "error getting category";
    }
    public static Category[] categories = {
            new Category("Bottles / Cans", 0, "I want to save", "bottles"),
            new Category("Water Fountain", 1, "I want to refill", "bottles"),
            new Category("Bike Rack", 2, "I want to bike", "times"),
            new Category("Coffee shop with discount", 3, "I want to save", "coffee cups"),
            new Category("Batteries / Electronic Waste", 4, "I want to recycle", "batteries/misc")
    };

}
