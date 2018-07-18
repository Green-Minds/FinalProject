package green_minds.com.finalproject.Model;

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
                return "Batteries / Electronic Waste";
            case 4:
                return "Coffee shop with discount";
        }
        return "error getting category";
    }
}
