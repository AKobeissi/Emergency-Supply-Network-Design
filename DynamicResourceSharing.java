import java.util.*;

public class DynamicResourceSharing {
    private List<City> cities;
    private Map<String, String> parent; // Union-Find Parent Map
    private Map<String, Integer> rank; // Union-Find Rank Map

    public DynamicResourceSharing(List<City> cities) {
        this.cities = cities;
        this.parent = new HashMap<>();
        this.rank = new HashMap<>();

        // Initialize Union-Find structures
        for (City city : cities) {
            parent.put(city.getName(), city.getName());
            rank.put(city.getName(), 0);
        }
    }

    // Union operation with Union by Rank
    // Union operation with Union by Rank and Path Compression
public void union(List<String> cityNames) {
    for (int i = 0; i < cityNames.size(); i++) {
        for (int j = i + 1; j < cityNames.size(); j++) {
            String city1 = cityNames.get(i);
            String city2 = cityNames.get(j);

            // Get allocated warehouses for both cities
            Set<Integer> warehousesCity1 = cities.stream()
                .filter(c -> c.getName().equals(city1))
                .findFirst()
                .get()
                .getAllocatedWarehouses();

            Set<Integer> warehousesCity2 = cities.stream()
                .filter(c -> c.getName().equals(city2))
                .findFirst()
                .get()
                .getAllocatedWarehouses();

            // Check if both cities have the exact same set of warehouses
            if (warehousesCity1.equals(warehousesCity2)) {
                String root1 = find(city1);
                String root2 = find(city2);

                if (!root1.equals(root2)) {
                    // Union by Rank
                    if (rank.get(root1) > rank.get(root2)) {
                        parent.put(root2, root1);
                        System.out.printf("Merged %s into %s (by rank)\n", city2, city1);
                    } else if (rank.get(root1) < rank.get(root2)) {
                        parent.put(root1, root2);
                        System.out.printf("Merged %s into %s (by rank)\n", city1, city2);
                    } else {
                        parent.put(root2, root1);
                        rank.put(root1, rank.get(root1) + 1);
                        System.out.printf("Merged %s into %s (equal rank, incrementing rank)\n", city2, city1);
                    }
                }
            } else {
                System.out.printf("No merge: %s and %s do not share identical resources.\n", city1, city2);
            }
        }
    }
}


    // Find operation with Path Compression
    public String find(String city) {
        if (!city.equals(parent.get(city))) {
            parent.put(city, find(parent.get(city))); // Path compression
        }
        return parent.get(city);
    }

    // Process Resource Sharing
    public Map<String, Object> processResourceSharing() {
        Map<String, Object> result = new LinkedHashMap<>();
        System.out.println("Task 4: Dynamic Resource Sharing Among Cities");
        System.out.println("Output:");
    
        // Step 1: Initial Clusters
        System.out.println("Initial Clusters:");
        Map<String, String> initialClusters = new LinkedHashMap<>();
        for (City city : cities) {
            String cluster = "Cluster " + city.getId();
            initialClusters.put(city.getName(), cluster);
            System.out.printf("City %s belongs to cluster: %d\n", city.getName(), city.getId());
        }
    
        // Step 2: Perform Union Based on Shared Resources
        List<String> cityNames = new ArrayList<>();
        for (City city : cities) {
            cityNames.add(city.getName());
        }
        union(cityNames);
    
        // Step 3: Merging Steps
        
        System.out.println("\nMerging clusters of City 1 and City 2...");
        List<Map<String, Object>> mergingSteps = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                String city1 = cities.get(i).getName();
                String city2 = cities.get(j).getName();

                if (find(city1).equals(find(city2))) {
                    // Find the cluster leader and get its numeric ID
                    String clusterLeader = find(city1);
                    int clusterId = cities.stream()
                        .filter(c -> c.getName().equals(clusterLeader))
                        .findFirst()
                        .map(City::getId)
                        .orElse(-1);

                    Map<String, Object> mergeStep = new LinkedHashMap<>();
                    mergeStep.put("Action", "Merge");
                    mergeStep.put("Cities", Arrays.asList(city1, city2));
                    mergeStep.put("Cluster After Merge", "Cluster " + clusterId);
                    mergingSteps.add(mergeStep);
                }
            }
        }

    
        // Step 4: Final Cluster Membership
        System.out.println("Final Cluster Membership:");
        Map<String, String> clusterMembershipAfterMerging = new LinkedHashMap<>();
        for (City city : cities) {
            String clusterLeader = find(city.getName());
            int clusterId = cities.stream()
                .filter(c -> c.getName().equals(clusterLeader))
                .findFirst()
                .map(City::getId)
                .orElse(-1);
    
            clusterMembershipAfterMerging.put(city.getName(), "Cluster " + clusterId);
            System.out.printf("City %s belongs to cluster: %d\n", city.getName(), clusterId);
        }
    
        // Step 5: Queries
        System.out.println("\nQueries:");
        List<Map<String, Object>> queries = new ArrayList<>();
        if (cities.size() >= 3) {
            String[][] queryPairs = {
                {cities.get(0).getName(), cities.get(2).getName()},
                {cities.get(0).getName(), cities.get(1).getName()},
                {cities.get(1).getName(), cities.get(2).getName()}
            };
    
            for (String[] pair : queryPairs) {
                Map<String, Object> query = createQuery(pair[0], pair[1]);
                queries.add(query);
    
                System.out.printf("Query: Are %s and %s in the same cluster?\n", pair[0], pair[1]);
                System.out.printf("%s\n", query.get("Result"));
            }
        }
    
        // Step 6: Prepare Final JSON Output
        Map<String, Object> sharing = new LinkedHashMap<>();
        sharing.put("Initial Clusters", initialClusters);
        sharing.put("Merging Steps", mergingSteps);
        sharing.put("Cluster Membership After Merging", clusterMembershipAfterMerging);
        sharing.put("Queries", queries);
    
        result.put("Dynamic Resource Sharing", sharing);
        return result;
    }

    private Map<String, Object> createQuery(String city1, String city2) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("Query", "Are " + city1 + " and " + city2 + " in the same cluster?");
        query.put("Result", find(city1).equals(find(city2)) ? "Yes" : "No");
        return query;
    }
    
}
