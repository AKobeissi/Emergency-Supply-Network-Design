// EmergencySupplyNetwork.java
import java.util.*;

public class EmergencySupplyNetwork {
    private List<City> cities;
    private List<Warehouse> warehouses;
    private double[][] costMatrix;

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

        // Print cost matrix
        System.out.println("Graph Representation (Cost Matrix):");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-8s | %-14s | %-14s | %-14s |\n", "cities", "Warehouse 101", "Warehouse 102", "Warehouse 103");
        System.out.println("---------------------------------------------------------------");
        
        for (int i = 0; i < cities.size(); i++) {
            System.out.printf("%-8s | %-14.2f | %-14.2f | %-14.2f |\n",
                cities.get(i).getName(),
                costMatrix[i][0],
                costMatrix[i][1],
                costMatrix[i][2]
            );
        }
        System.out.println("---------------------------------------------------------------\n");

        // Initialize remaining capacities
        for (Warehouse w : warehouses) {
            remainingCapacities.put(w.getId(), w.getCapacity());
        }

        // Sort cities by priority
        List<City> sortedCities = new ArrayList<>(cities);
        sortedCities.sort((c1, c2) -> {
            if (c1.getPriority() == c2.getPriority()) return 0;
            if (c1.getPriority() == City.Priority.HIGH) return -1;
            if (c2.getPriority() == City.Priority.HIGH) return 1;
            if (c1.getPriority() == City.Priority.MEDIUM) return -1;
            return 1;
        });

        // Allocate resources
        for (City city : sortedCities) {
            System.out.printf("Allocating resources for %s (Priority: %s)\n", 
                city.getName(), city.getPriority());
                
            Map<String, Object> allocation = new HashMap<>();
            allocation.put("City", city.getName());
            allocation.put("Priority", city.getPriority().toString());

            List<Map<String, Object>> allocatedUnits = new ArrayList<>();
            int remainingDemand = city.getDemand();
            
            while (remainingDemand > 0) {
                int bestWarehouseIndex = -1;
                double bestCost = Double.MAX_VALUE;
                
                for (int i = 0; i < warehouses.size(); i++) {
                    Warehouse w = warehouses.get(i);
                    if (remainingCapacities.get(w.getId()) > 0 && 
                        costMatrix[cities.indexOf(city)][i] < bestCost) {
                        bestWarehouseIndex = i;
                        bestCost = costMatrix[cities.indexOf(city)][i];
                    }
                }
                
                if (bestWarehouseIndex == -1) break;
                
                Warehouse bestWarehouse = warehouses.get(bestWarehouseIndex);
                int availableCapacity = remainingCapacities.get(bestWarehouse.getId());
                int allocatedAmount = Math.min(availableCapacity, remainingDemand);
                
                System.out.printf("Allocated %d units from %s\n", 
                    allocatedAmount, bestWarehouse.getName());
                
                Map<String, Object> units = new HashMap<>();
                units.put("Units", allocatedAmount);
                units.put("Warehouse", bestWarehouse.getName());
                allocatedUnits.add(units);
                
                remainingCapacities.put(bestWarehouse.getId(), 
                    availableCapacity - allocatedAmount);
                remainingDemand -= allocatedAmount;
            }
            
            if (allocatedUnits.size() == 1) {
                allocation.put("Allocated", allocatedUnits.get(0).get("Units"));
                allocation.put("Warehouse", allocatedUnits.get(0).get("Warehouse"));
            } else {
                allocation.put("Allocated", allocatedUnits);
            }
            
            allocationResults.add(allocation);
        }

        System.out.println("\nRemaining Warehouse Capacities:");
        for (Warehouse w : warehouses) {
            System.out.printf("%s: %d units\n", w.getName(), 
                remainingCapacities.get(w.getId()));
        }
        System.out.println();

        // Prepare result for JSON output
        Map<String, Object> graphRepresentation = new HashMap<>();
        List<Map<String, Object>> costMatrixOutput = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("City", cities.get(i).getName());
            for (int j = 0; j < warehouses.size(); j++) {
                row.put("Warehouse " + warehouses.get(j).getId(), 
                    String.format("%.2f", costMatrix[i][j]));
            }
            costMatrixOutput.add(row);
        }
        graphRepresentation.put("Cost Matrix", costMatrixOutput);

        result.put("Graph Representation", graphRepresentation);
        result.put("Resource Allocation", allocationResults);
        result.put("Remaining Capacities", remainingCapacities);

        return result;
    }
}
//    public Map<String, Object> allocateResources() {
//        Map<String, Object> result = new HashMap<>();
//        List<Map<String, Object>> allocationResults = new ArrayList<>();
//        Map<Integer, Integer> remainingCapacities = new HashMap<>();
//
//        // Initialize remaining capacities
//        for (Warehouse w : warehouses) {
//            remainingCapacities.put(w.getId(), w.getCapacity());
//        }
//
//        // Sort cities by priority
//        List<City> sortedCities = new ArrayList<>(cities);
//        sortedCities.sort((c1, c2) -> {
//            if (c1.getPriority() == c2.getPriority()) return 0;
//            if (c1.getPriority() == City.Priority.HIGH) return -1;
//            if (c2.getPriority() == City.Priority.HIGH) return 1;
//            if (c1.getPriority() == City.Priority.MEDIUM) return -1;
//            return 1;
//        });
//
//        // Allocate resources
//        for (City city : sortedCities) {
//            Map<String, Object> allocation = new HashMap<>();
//            allocation.put("City", city.getName());
//            allocation.put("Priority", city.getPriority().toString());
//
//            // Find best warehouse with available capacity
//            List<Map<String, Object>> allocatedUnits = new ArrayList<>();
//            int remainingDemand = city.getDemand();
//            
//            while (remainingDemand > 0) {
//                int bestWarehouseIndex = -1;
//                double bestCost = Double.MAX_VALUE;
//                
//                for (int i = 0; i < warehouses.size(); i++) {
//                    Warehouse w = warehouses.get(i);
//                    if (remainingCapacities.get(w.getId()) > 0 && 
//                        costMatrix[cities.indexOf(city)][i] < bestCost) {
//                        bestWarehouseIndex = i;
//                        bestCost = costMatrix[cities.indexOf(city)][i];
//                    }
//                }
//                
//                if (bestWarehouseIndex == -1) break;
//                
//                Warehouse bestWarehouse = warehouses.get(bestWarehouseIndex);
//                int availableCapacity = remainingCapacities.get(bestWarehouse.getId());
//                int allocatedAmount = Math.min(availableCapacity, remainingDemand);
//                
//                Map<String, Object> units = new HashMap<>();
//                units.put("Units", allocatedAmount);
//                units.put("Warehouse", bestWarehouse.getName());
//                allocatedUnits.add(units);
//                
//                remainingCapacities.put(bestWarehouse.getId(), 
//                    availableCapacity - allocatedAmount);
//                remainingDemand -= allocatedAmount;
//            }
//            
//            if (allocatedUnits.size() == 1) {
//                allocation.put("Allocated", allocatedUnits.get(0).get("Units"));
//                allocation.put("Warehouse", allocatedUnits.get(0).get("Warehouse"));
//            } else {
//                allocation.put("Allocated", allocatedUnits);
//            }
//            
//            allocationResults.add(allocation);
//        }
//
//        // Prepare cost matrix for output
//        List<Map<String, Object>> costMatrixOutput = new ArrayList<>();
//        for (int i = 0; i < cities.size(); i++) {
//            Map<String, Object> row = new HashMap<>();
//            row.put("City", cities.get(i).getName());
//            for (int j = 0; j < warehouses.size(); j++) {
//                row.put("Warehouse " + warehouses.get(j).getId(), 
//                    String.format("%.2f", costMatrix[i][j]));
//            }
//            costMatrixOutput.add(row);
//        }
//
//        Map<String, Object> graphRepresentation = new HashMap<>();
//        graphRepresentation.put("Cost Matrix", costMatrixOutput);
//
//        result.put("Graph Representation", graphRepresentation);
//        result.put("Resource Allocation", allocationResults);
//        result.put("Remaining Capacities", remainingCapacities);
//
//        return result;
//    }
//
//    public double[][] getCostMatrix() {
//        return costMatrix;
//    }
//}
