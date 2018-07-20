package green_minds.com.finalproject.model;

public class PinCategoryHelper {
    public static String getPinIdentifier(int i){
        switch (i){
            case 0:
                return "Bottles / Cans";
            case 1:
                return "Water Fountain";
            case 2:
                return "Bike Rack";
            case 3:
                return "Coffee shop with discount";
            case 4:
                return "Batteries / Electronic Waste";
        }
        return "error getting category";
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
