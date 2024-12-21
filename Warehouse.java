// Warehouse.java
public class Warehouse {
    private int id;
    private double x;
    private double y;
    private int capacity;
    private String name;

    public Warehouse(String name, int id, double x, double y, int capacity) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getName() { return name; }
}
