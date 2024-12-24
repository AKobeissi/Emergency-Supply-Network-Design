// NetworkApp.java - continued from previous implementation
import java.io.*;
import java.util.*;

public class NetworkApp {
    public static void main(String[] args) {
        processTestCase("TestCase0.txt", "Output_testCase0.json");
        processTestCase("TestCase1.txt", "Output_testCase1.json");
        processTestCase("TestCase2.txt", "Output_testCase2.json");
    }

    public static void processTestCase(String inputFile, String outputFile) {
        try {
            /*List<City> cities = new ArrayList<>();
            List<Warehouse> warehouses = new ArrayList<>();
            parseInputFile(inputFile, cities, warehouses);
    
            // Pass the same warehouse reference to both classes
            EmergencySupplyNetwork network = new EmergencySupplyNetwork(cities, warehouses);
            ResourceRedistribution redistribution = new ResourceRedistribution(warehouses);
            DynamicResourceSharing sharing = new DynamicResourceSharing(cities);
    
            Map<String, Object> result = new HashMap<>();
            
            // Task 2: Resource Allocation
            Map<String, Object> networkResult = network.allocateResources();
            result.put("Task 1 and 2", networkResult);
            
    
            // Task 3: Resource Redistribution
            Map<String, Object> redistributionResult = redistribution.redistributeResources();
            result.put("Task 3", redistributionResult);
    
            // Task 4: Dynamic Resource Sharing
            Map<String, Object> sharingResult = sharing.processResourceSharing();
            result.put("Task 4", sharingResult);
    
            // Write output to JSON file
            writeJsonOutput(result, outputFile);*/

            List<City> cities = new ArrayList<>();
            List<Warehouse> warehouses = new ArrayList<>();
            parseInputFile(inputFile, cities, warehouses);

            EmergencySupplyNetwork network = new EmergencySupplyNetwork(cities, warehouses);
            ResourceRedistribution redistribution = new ResourceRedistribution(warehouses);
            DynamicResourceSharing sharing = new DynamicResourceSharing(cities);

            Map<String, Object> result = new LinkedHashMap<>();

            // Task 1 and 2: Resource Allocation
            Map<String, Object> networkResult = network.allocateResources();
            Map<String, Object> task1And2 = new LinkedHashMap<>();
            
            // Graph Representation
            Map<String, Object> graphRepresentation = new LinkedHashMap<>();
            List<Map<String, Object>> rawMatrix = (List<Map<String, Object>>) networkResult.get("Graph Representation");
            List<Map<String, Object>> costMatrix = new ArrayList<>();
            for (Map<String, Object> row : rawMatrix) {
                Map<String, Object> formattedRow = new LinkedHashMap<>();
                formattedRow.put("City", "City " + row.get("City ID"));
                formattedRow.put("Warehouse 101", Double.parseDouble(row.get("Warehouse 101").toString()));
                formattedRow.put("Warehouse 102", Double.parseDouble(row.get("Warehouse 102").toString()));
                formattedRow.put("Warehouse 103", Double.parseDouble(row.get("Warehouse 103").toString()));
                costMatrix.add(formattedRow);
            }
            graphRepresentation.put("Cost Matrix", costMatrix);
            task1And2.put("Graph Representation", graphRepresentation);

            // Resource Allocation
            List<Map<String, Object>> rawAllocations = (List<Map<String, Object>>) networkResult.get("Resource Allocation");
            List<Map<String, Object>> formattedAllocations = new ArrayList<>();
            for (Map<String, Object> allocation : rawAllocations) {
                Map<String, Object> formatted = new LinkedHashMap<>();
                formatted.put("City", "City " + allocation.get("City ID"));
                formatted.put("Priority", allocation.get("Priority"));
                List<Map<String, Object>> allocUnits = (List<Map<String, Object>>) allocation.get("Allocated");
                if (allocUnits.size() == 1) {
                    formatted.put("Allocated", allocUnits.get(0).get("Units"));
                    formatted.put("Warehouse", "Warehouse " + allocUnits.get(0).get("Warehouse ID"));
                } else {
                    formatted.put("Allocated", allocUnits.stream().map(unit -> {
                        Map<String, Object> unitMap = new LinkedHashMap<>();
                        unitMap.put("Warehouse", "Warehouse " + unit.get("Warehouse ID"));
                        unitMap.put("Units", unit.get("Units"));
                        return unitMap;
                    }).toList());
                }
                formattedAllocations.add(formatted);
            }
            task1And2.put("Resource Allocation", formattedAllocations);

            // Remaining Capacities
            Map<Integer, Integer> remainingCapacities = (Map<Integer, Integer>) networkResult.get("Remaining Capacities");
            Map<String, Integer> formattedCapacities = new LinkedHashMap<>();
            for (Map.Entry<Integer, Integer> entry : remainingCapacities.entrySet()) {
                formattedCapacities.put("Warehouse " + entry.getKey(), entry.getValue());
            }
            task1And2.put("Remaining Capacities", formattedCapacities);
            result.put("Task 1 and 2", task1And2);

            // Task 3: Resource Redistribution
            Map<String, Object> redistributionResult = redistribution.redistributeResources();
            Map<String, Object> task3 = new LinkedHashMap<>();
            List<Map<String, Object>> rawTransfers = (List<Map<String, Object>>) redistributionResult.get("Transfers");
            List<Map<String, Object>> formattedTransfers = new ArrayList<>();
            for (Map<String, Object> transfer : rawTransfers) {
                Map<String, Object> formatted = new LinkedHashMap<>();
                formatted.put("From", "Warehouse " + transfer.get("From Warehouse ID"));
                formatted.put("To", "Warehouse " + transfer.get("To Warehouse ID"));
                formatted.put("Units", transfer.get("Transferred Units"));
                formattedTransfers.add(formatted);
            }
            task3.put("Transfers", formattedTransfers);

            Map<String, Integer> finalLevels = (Map<String, Integer>) redistributionResult.get("Final Resource Levels");
            Map<String, Integer> formattedLevels = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : finalLevels.entrySet()) {
                formattedLevels.put(entry.getKey().replace("Warehouse ID: ", "Warehouse "), entry.getValue());
            }
            task3.put("Final Resource Levels", formattedLevels);
            result.put("Task 3", Map.of("Resource Redistribution", task3));

            // Task 4: Dynamic Resource Sharing
            Map<String, Object> sharingResult = sharing.processResourceSharing();
            result.put("Task 4", sharingResult);
            // Write to JSON
            writeJsonOutput(result, outputFile);

        } catch (IOException e) {
            System.err.println("Error processing test case: " + e.getMessage());
        }
    }
    

    private static void parseInputFile(String filename, List<City> cities, List<Warehouse> warehouses) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean readingCities = false;
            boolean readingWarehouses = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("Cities:")) {
                    readingCities = true;
                    readingWarehouses = false;
                    continue;
                } else if (line.equals("Warehouses:")) {
                    readingCities = false;
                    readingWarehouses = true;
                    continue;
                }

                if (line.isEmpty()) continue;

                if (readingCities) {
                    parseCityLine(line, cities);
                } else if (readingWarehouses) {
                    parseWarehouseLine(line, warehouses);
                }
            }
        }
    }

    private static void parseCityLine(String line, List<City> cities) {
        try {
            // Regular expressions to extract information
            String namePattern = "City\\s+\\w+";
            String idPattern = "ID\\s*=\\s*(\\d+)";
            String coordsPattern = "Coordinates\\s*=\\s*\\((\\d+),\\s*(\\d+)\\)";
            String demandPattern = "Demand\\s*=\\s*(\\d+)";
            String priorityPattern = "Priority\\s*=\\s*(\\w+)";

            // Extract data using regex
            String name = extractMatch(line, namePattern);
            int id = Integer.parseInt(extractMatch(line, idPattern, 1));
            
            java.util.regex.Matcher coordsMatcher = java.util.regex.Pattern.compile(coordsPattern).matcher(line);
            double x = 0, y = 0;
            if (coordsMatcher.find()) {
                x = Double.parseDouble(coordsMatcher.group(1));
                y = Double.parseDouble(coordsMatcher.group(2));
            }
            
            int demand = Integer.parseInt(extractMatch(line, demandPattern, 1));
            String priorityStr = extractMatch(line, priorityPattern, 1);
            
            City.Priority priority;
            if (priorityStr.equals("High")) {
                priority = City.Priority.HIGH;
            } else if (priorityStr.equals("Medium")) {
                priority = City.Priority.MEDIUM;
            } else {
                priority = City.Priority.LOW;
            }

            cities.add(new City(name, id, x, y, demand, priority));
        } catch (Exception e) {
            System.err.println("Error parsing city line: " + line + "\nDetails: " + e.getMessage());
        }
    }

    private static void parseWarehouseLine(String line, List<Warehouse> warehouses) {
        try {
            // Regular expressions to extract information
            String namePattern = "Warehouse\\s+\\w+";
            String idPattern = "ID\\s*=\\s*(\\d+)";
            String coordsPattern = "Coordinates\\s*=\\s*\\((\\d+),\\s*(\\d+)\\)";
            String capacityPattern = "Capacity\\s*=\\s*(\\d+)";

            // Extract data using regex
            String name = extractMatch(line, namePattern);
            int id = Integer.parseInt(extractMatch(line, idPattern, 1));
            
            java.util.regex.Matcher coordsMatcher = java.util.regex.Pattern.compile(coordsPattern).matcher(line);
            double x = 0, y = 0;
            if (coordsMatcher.find()) {
                x = Double.parseDouble(coordsMatcher.group(1));
                y = Double.parseDouble(coordsMatcher.group(2));
            }
            
            int capacity = Integer.parseInt(extractMatch(line, capacityPattern, 1));

            warehouses.add(new Warehouse(name, id, x, y, capacity));
        } catch (Exception e) {
            System.err.println("Error parsing warehouse line: " + line + "\nDetails: " + e.getMessage());
        }
    }

    private static String extractMatch(String input, String pattern) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(input);
        return matcher.find() ? matcher.group(0) : "";
    }

    private static String extractMatch(String input, String pattern, int group) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(input);
        return matcher.find() ? matcher.group(group) : "";
    }

    private static void writeJsonOutput(Map<String, Object> data, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(convertToJson(data, 0));
        }
    }

    private static String convertToJson(Object obj, int indent) {
        if (obj == null) return "null";
        
        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj, indent);
        } else if (obj instanceof List) {
            return listToJson((List<?>) obj, indent);
        } else if (obj instanceof String) {
            return "\"" + escapeJsonString((String) obj) + "\"";
        } else if (obj instanceof Number) {
            return obj.toString();
        } else if (obj instanceof Boolean) {
            return obj.toString();
        }
        return "\"" + obj.toString() + "\"";
    }

    private static String mapToJson(Map<?, ?> map, int indent) {
        if (map.isEmpty()) return "{}";
        
        StringBuilder sb = new StringBuilder("{\n");
        String indentStr = "    ".repeat(indent + 1);
        
        Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
            sb.append(indentStr)
              .append("\"").append(entry.getKey()).append("\": ")
              .append(convertToJson(entry.getValue(), indent + 1));
            
            if (it.hasNext()) sb.append(",");
            sb.append("\n");
        }
        
        sb.append("    ".repeat(indent)).append("}");
        return sb.toString();
    }

    private static String listToJson(List<?> list, int indent) {
        if (list.isEmpty()) return "[]";
        
        StringBuilder sb = new StringBuilder("[\n");
        String indentStr = "    ".repeat(indent + 1);
        
        for (int i = 0; i < list.size(); i++) {
            sb.append(indentStr)
              .append(convertToJson(list.get(i), indent + 1));
            
            if (i < list.size() - 1) sb.append(",");
            sb.append("\n");
        }
        
        sb.append("    ".repeat(indent)).append("]");
        return sb.toString();
    }

    private static String escapeJsonString(String str) {
        return str.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
    }
}
