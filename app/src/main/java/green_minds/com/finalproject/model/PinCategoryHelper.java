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
}
