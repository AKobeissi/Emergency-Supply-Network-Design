// City.java
public class City {
    private int id;
    private double x;
    private double y;
    private int demand;
    private Priority priority;
    private String name;

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    public City(String name, int id, double x, double y, int demand, Priority priority) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        this.demand = demand;
        this.priority = priority;
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getDemand() { return demand; }
    public Priority getPriority() { return priority; }
    public String getName() { return name; }
}
