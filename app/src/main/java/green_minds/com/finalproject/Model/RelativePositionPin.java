package green_minds.com.finalproject.Model;

public class RelativePositionPin extends Pin{

    private Double distanceAwayFromCurrent; //in meters

    public RelativePositionPin(){
        super();
    }

    public void setDistanceAway(Double dist){
        distanceAwayFromCurrent = dist;
    }

    public Double getDistanceAway() {
        return distanceAwayFromCurrent;
    }

    public Double getDistanceAwayinMiles() {
        return metersToMiles(distanceAwayFromCurrent);
    }

    public static Double metersToMiles(Double meters){
        return meters * 0.000621371;
    }
}
