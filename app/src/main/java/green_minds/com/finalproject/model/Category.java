package green_minds.com.finalproject.model;

public class Category {
    private String name;
    private int typeKey;
    private String description;
    private String unit;

    public Category(String n, int t, String d, String u){
        this.name = n;
        this.typeKey = t;
        this.description = d;
        this.unit = u;
    }

    public String getName() {
        return name;
    }

    public int getTypeKey() {
        return typeKey;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

}
