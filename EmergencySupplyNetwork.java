// EmergencySupplyNetwork.java
import java.util.*;

public class EmergencySupplyNetwork {
    private List<City> cities;    // List of cities that require resources 
    private List<Warehouse> warehouses;   // List of warehouses supplying resources
    private double[][] costMatrix;   // Cost matrix for transport costs between cities and warehouses
    
    // Constructor: Initializes cities and warehouses, and calculates the cost matrix
    public EmergencySupplyNetwork(List<City> cities, List<Warehouse> warehouses) {
        this.cities = cities;
        this.warehouses = warehouses;
        this.costMatrix = new double[cities.size()][warehouses.size()];
        calculateCostMatrix();
    }

    private void calculateCostMatrix() {
        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < warehouses.size(); j++) {
                costMatrix[i][j] = calculateTransportCost(cities.get(i), warehouses.get(j));
            }
        }
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private double calculateTransportCost(City city, Warehouse warehouse) {
        double distance = calculateDistance(city.getX(), city.getY(), warehouse.getX(), warehouse.getY());
        double coefficient;
        
        if (distance <= 10) {
            coefficient = 1; // Drone
        } else if (distance <= 20) {
            coefficient = 2; // Truck
        } else {
            coefficient = 3; // Rail
        }
        
        return distance * coefficient;
    }

    public Map<String, Object> allocateResources() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> allocationResults = new ArrayList<>();
        Map<Integer, Integer> remainingCapacities = new HashMap<>();
    
        // Step 1: Print the Cost Matrix with City and Warehouse IDs
        System.out.println("Graph Representation (Cost Matrix):");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-8s | %-14s | %-14s | %-14s |\n", "City ID", "Warehouse 101", "Warehouse 102", "Warehouse 103");
        System.out.println("---------------------------------------------------------------");
    
        List<Map<String, Object>> costMatrixOutput = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("City ID", cities.get(i).getId());
            for (int j = 0; j < warehouses.size(); j++) {
                row.put("Warehouse " + warehouses.get(j).getId(),
                    String.format("%.2f", costMatrix[i][j]));
            }
            costMatrixOutput.add(row);
    
            System.out.printf("%-8d | %-14.2f | %-14.2f | %-14.2f |\n",
                cities.get(i).getId(),
                costMatrix[i][0],
                costMatrix[i][1],
                costMatrix[i][2]
            );
        }
        System.out.println("---------------------------------------------------------------\n");
    
        // Step 2: Sort cities by priority (High > Medium > Low)
        List<City> sortedCities = new ArrayList<>(cities);
        sortedCities.sort((c1, c2) -> c1.getPriority().compareTo(c2.getPriority()));
    
        // Step 3: Allocate resources dynamically
        for (City city : sortedCities) {
            System.out.printf("Allocating resources for City ID: %d (Priority: %s)\n", 
                city.getId(), city.getPriority());
    
            Map<String, Object> allocation = new HashMap<>();
            allocation.put("City ID", city.getId());
            allocation.put("Priority", city.getPriority().toString());
    
            List<Map<String, Object>> allocatedUnits = new ArrayList<>();
            int remainingDemand = city.getDemand();
    
            // Sort warehouses dynamically based on cost for this city
            List<Warehouse> sortedWarehouses = new ArrayList<>(warehouses);
            sortedWarehouses.sort(Comparator.comparingDouble(
                w -> costMatrix[cities.indexOf(city)][warehouses.indexOf(w)]
            ));
    
            // Explicitly iterate through all warehouses
            for (Warehouse warehouse : sortedWarehouses) {
                if (remainingDemand <= 0) {
                    break; // Stop if demand is fully met
                }
    
                int availableCapacity = warehouse.getCapacity(); // Directly fetch from Warehouse object
                if (availableCapacity > 0) {
                    int allocatedAmount = Math.min(availableCapacity, remainingDemand);
    
                    System.out.printf("Allocated %d units from Warehouse ID: %d (Remaining Capacity: %d)\n", 
                        allocatedAmount, warehouse.getId(), availableCapacity - allocatedAmount);
    
                    Map<String, Object> allocationStep = new HashMap<>();
                    allocationStep.put("Units", allocatedAmount);
                    allocationStep.put("Warehouse ID", warehouse.getId());
                    allocatedUnits.add(allocationStep);
                    city.addAllocatedWarehouse(warehouse.getId());

    
                    remainingDemand -= allocatedAmount;
                    warehouse.setCapacity(availableCapacity - allocatedAmount); // Update Warehouse directly
                    remainingCapacities.put(warehouse.getId(), availableCapacity - allocatedAmount);
    
                    
                    city.addAllocatedWarehouse(warehouse.getId());
                }
            }
    
            // Warning if demand couldn't be fully met
            if (remainingDemand > 0) {
                System.out.printf("Warning: Could not fully meet demand for City ID: %d. Remaining demand: %d units.\n", 
                    city.getId(), remainingDemand);
            }
    
            allocation.put("Allocated", allocatedUnits);
            allocationResults.add(allocation);
        }
    
        // Step 4: Print remaining warehouse capacities
        System.out.println("\nRemaining Warehouse Capacities:");
        for (Warehouse w : warehouses) {
            System.out.printf("Warehouse ID: %d: %d units\n", w.getId(), w.getCapacity());
        }
        System.out.println();
    
        result.put("Graph Representation", costMatrixOutput);
        result.put("Resource Allocation", allocationResults);
        result.put("Remaining Capacities", remainingCapacities);
    
        return result;
    }
}
