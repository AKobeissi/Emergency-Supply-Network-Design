import java.util.*;

public class ResourceRedistribution {
    private List<Warehouse> warehouses;

    public ResourceRedistribution(List<Warehouse> warehouses) {
        this.warehouses = warehouses;
    }

    public Map<String, Object> redistributeResources() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> transfers = new ArrayList<>();

        System.out.println("\nTask 3: Resource Redistribution Using Heap Structure");
        System.out.println("Output:");

        // Step 1: Create Surplus and Need Heaps
        PriorityQueue<Warehouse> surplus = new PriorityQueue<>(
            (w1, w2) -> w2.getCapacity() - w1.getCapacity()
        );

        PriorityQueue<Warehouse> need = new PriorityQueue<>(
            Comparator.comparingInt(Warehouse::getCapacity)
        );

        // Step 2: Classify Warehouses into Surplus and Need
        for (Warehouse w : warehouses) {
            if (w.getCapacity() > 50) {
                surplus.add(w);
            } else if (w.getCapacity() < 50) {
                need.add(w);
            }
        }

        // Step 3: Perform Transfers
        while (!surplus.isEmpty() && !need.isEmpty()) {
            Warehouse from = surplus.poll();
            Warehouse to = need.poll();

            int surplusAmount = from.getCapacity() - 50; // Extra above 50
            int needAmount = 50 - to.getCapacity(); // Amount needed to reach 50
            int transferAmount = Math.min(surplusAmount, needAmount);

            if (transferAmount > 0) {
                System.out.printf("Transferred %d units from Warehouse ID: %d to Warehouse ID: %d.\n", 
                    transferAmount, from.getId(), to.getId());

                from.setCapacity(from.getCapacity() - transferAmount);
                to.setCapacity(to.getCapacity() + transferAmount);

                Map<String, Object> transferLog = new HashMap<>();
                transferLog.put("From Warehouse ID", from.getId());
                transferLog.put("To Warehouse ID", to.getId());
                transferLog.put("Transferred Units", transferAmount);
                transfers.add(transferLog);

                // Re-add warehouses to heaps if they still meet surplus or need conditions
                if (from.getCapacity() > 50) surplus.add(from);
                if (to.getCapacity() < 50) need.add(to);
            }
        }

        // Step 4: Display Final Resource Levels
        System.out.println("Final Resource Levels:");
        Map<String, Integer> finalLevels = new HashMap<>();
        for (Warehouse w : warehouses) {
            System.out.printf("Warehouse ID: %d: %d units\n", w.getId(), w.getCapacity());
            finalLevels.put("Warehouse ID: " + w.getId(), w.getCapacity());
        }

        result.put("Transfers", transfers);
        result.put("Final Resource Levels", finalLevels);

        return result;
    }
}
