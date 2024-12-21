// ResourceRedistribution.java
import java.util.*;

public class ResourceRedistribution {
    private List<Warehouse> warehouses;
    private static final int TARGET_LEVEL = 50;

    public ResourceRedistribution(List<Warehouse> warehouses) {
        this.warehouses = warehouses;
    }

    public Map<String, Object> redistributeResources() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> transfers = new ArrayList<>();
        
        System.out.println("Task 3: Resource Redistribution Using Heap Structure");
        System.out.println("Output:");
        
        PriorityQueue<Warehouse> surplus = new PriorityQueue<>(
            (w1, w2) -> w2.getCapacity() - w1.getCapacity()
        );
        
        PriorityQueue<Warehouse> need = new PriorityQueue<>(
            Comparator.comparingInt(Warehouse::getCapacity)
        );

        for (Warehouse w : warehouses) {
            if (w.getCapacity() > TARGET_LEVEL) {
                surplus.add(w);
            } else if (w.getCapacity() < TARGET_LEVEL) {
                need.add(w);
            }
        }

        while (!surplus.isEmpty() && !need.isEmpty()) {
            Warehouse from = surplus.peek();
            Warehouse to = need.peek();
            
            int availableSurplus = from.getCapacity() - TARGET_LEVEL;
            int required = TARGET_LEVEL - to.getCapacity();
            int transfer = Math.min(availableSurplus, required);
            
            if (transfer > 0) {
                System.out.printf("Transferred %d units from %s to %s.\n", 
                    transfer, from.getName(), to.getName());
                
                Map<String, Object> transferInfo = new HashMap<>();
                transferInfo.put("From", from.getName());
                transferInfo.put("To", to.getName());
                transferInfo.put("Units", transfer);
                transfers.add(transferInfo);
                
                from.setCapacity(from.getCapacity() - transfer);
                to.setCapacity(to.getCapacity() + transfer);
                
                if (from.getCapacity() <= TARGET_LEVEL) surplus.poll();
                if (to.getCapacity() >= TARGET_LEVEL) need.poll();
            }
        }

        System.out.println("\nFinal Resource Levels:");
        Map<String, Integer> finalLevels = new HashMap<>();
        for (Warehouse w : warehouses) {
            System.out.printf("%s: %d units\n", w.getName(), w.getCapacity());
            finalLevels.put(w.getName(), w.getCapacity());
        }
        System.out.println();

        Map<String, Object> redistribution = new HashMap<>();
        redistribution.put("Transfers", transfers);
        redistribution.put("Final Resource Levels", finalLevels);
        
        result.put("Resource Redistribution", redistribution);
        return result;
    }
}