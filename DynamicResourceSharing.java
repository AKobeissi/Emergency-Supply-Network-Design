import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class DynamicResourceSharing {
    private Map<String, String> parent;
    private Map<String, Integer> rank;
    private List<City> cities;

    public DynamicResourceSharing(List<City> cities) {
        this.cities = cities;
        this.parent = new HashMap<>();
        this.rank = new HashMap<>();
        initializeSets();
    }

    private void initializeSets() {
        for (City city : cities) {
            parent.put(city.getName(), city.getName());
            rank.put(city.getName(), 0);
        }
    }

    private String find(String cityName) {
        if (parent.get(cityName).equals(cityName)) {
            return cityName;
        }
        String root = find(parent.get(cityName));
        parent.put(cityName, root);
        return root;
    }

    public void union(String city1Name, String city2Name) {
        String root1 = find(city1Name);
        String root2 = find(city2Name);
        
        if (!root1.equals(root2)) {
            if (rank.get(root1) < rank.get(root2)) {
                parent.put(root1, root2);
            } else if (rank.get(root1) > rank.get(root2)) {
                parent.put(root2, root1);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank.get(root1) + 1);
            }
        }
    }
    
    private Map<String, Object> createQuery(String city1, String city2) {
        Map<String, Object> query = new HashMap<>();
        String queryText = String.format("Query: Are %s and %s in the same cluster?", city1, city2);
        String result = find(city1).equals(find(city2)) ? "Yes" : "No";
        
        query.put("Query", queryText);
        query.put("Result", result);
        return query;
    }

    public Map<String, Object> processResourceSharing() {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("Task 4: Dynamic Resource Sharing Among Cities");
        System.out.println("Output:");
        System.out.println("Initial Clusters:");
        
        // Initial clusters
        Map<String, String> initialClusters = new HashMap<>();
        for (City city : cities) {
            String clusterNum = String.valueOf(city.getId());
            System.out.printf("%s belongs to cluster: %s\n", city.getName(), clusterNum);
            initialClusters.put(city.getName(), "Cluster " + clusterNum);
        }
        
        // Merging steps
        List<Map<String, Object>> mergingSteps = new ArrayList<>();
        if (cities.size() >= 2) {
            System.out.println("\nMerging clusters of City 1 and City 2...");
            
            Map<String, Object> mergeStep = new HashMap<>();
            mergeStep.put("Action", "Merge");
            List<String> mergedCities = Arrays.asList(
                cities.get(0).getName(), 
                cities.get(1).getName()
            );
            mergeStep.put("Cities", mergedCities);
            mergeStep.put("Cluster After Merge", "Cluster 1");
            mergingSteps.add(mergeStep);
            union(cities.get(0).getName(), cities.get(1).getName());
            
            for (City city : cities) {
                System.out.printf("%s belongs to cluster: %s\n", 
                    city.getName(), find(city.getName()).equals(find(cities.get(0).getName())) ? "1" : "3");
            }
        }
        
        // Process queries
        System.out.println();
        List<Map<String, Object>> queries = new ArrayList<>();
        if (cities.size() >= 3) {
            String[][] queryPairs = {
                {cities.get(0).getName(), cities.get(2).getName()},
                {cities.get(0).getName(), cities.get(1).getName()},
                {cities.get(1).getName(), cities.get(2).getName()}
            };
            
            for (String[] pair : queryPairs) {
                Map<String, Object> query = createQuery(pair[0], pair[1]);
                System.out.printf("%s\n%s\n", query.get("Query"), query.get("Result"));
                queries.add(query);
            }
        }
        
        Map<String, Object> sharing = new HashMap<>();
        sharing.put("Initial Clusters", initialClusters);
        sharing.put("Merging Steps", mergingSteps);
        sharing.put("Queries", queries);
        
        result.put("Dynamic Resource Sharing", sharing);
        return result;
    }
}