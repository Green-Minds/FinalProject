package green_minds.com.finalproject.model;


import android.support.annotation.NonNull;

public class RelativePositionPin implements Comparable<RelativePositionPin>{

    private Double distanceAwayFromCurrent; //in meters
    private Pin pin;

    public RelativePositionPin(Pin pin){
        this.pin = pin;
    }

    public void setDistanceAway(Double dist){
        distanceAwayFromCurrent = dist;
    }

    public Pin getPin(){
        return pin;
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

    @Override
    public int compareTo(@NonNull RelativePositionPin o) {
        if(this.getDistanceAway() - o.getDistanceAway() < 0){
            return -1;
        } else if (this.getDistanceAway() - o.getDistanceAway() > 0){
            return 1;
        } else{
            return 0;
        }
    }
}
