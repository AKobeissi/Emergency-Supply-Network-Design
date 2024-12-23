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
            List<City> cities = new ArrayList<>();
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
//    private static void parseCityLine(String line, List<City> cities) {
//        try {
//            String[] parts = line.split(",");
//            String name = parts[0].split(":")[0].trim();
//            int id = Integer.parseInt(parts[0].split("=")[1].trim());
//            
//            String coordsStr = parts[1].split("=")[1].trim();
//            double x = Double.parseDouble(coordsStr.substring(1, coordsStr.indexOf(",")).trim());
//            double y = Double.parseDouble(coordsStr.substring(coordsStr.indexOf(",") + 1, coordsStr.indexOf(")")).trim());
//            
//            int demand = Integer.parseInt(parts[2].split("=")[1].trim().split(" ")[0]);
//            
//            String priorityStr = parts[3].split("=")[1].trim();
//            City.Priority priority;
//            if (priorityStr.equals("High")) {
//                priority = City.Priority.HIGH;
//            } else if (priorityStr.equals("Medium")) {
//                priority = City.Priority.MEDIUM;
//            } else {
//                priority = City.Priority.LOW;
//            }
//
//            cities.add(new City(name, id, x, y, demand, priority));
//        } catch (Exception e) {
//            System.err.println("Error parsing city line: " + line);
//        }
//    }
//
//    private static void parseWarehouseLine(String line, List<Warehouse> warehouses) {
//        try {
//            String[] parts = line.split(",");
//            String name = parts[0].split(":")[0].trim();
//            int id = Integer.parseInt(parts[0].split("=")[1].trim());
//            
//            String coordsStr = parts[1].split("=")[1].trim();
//            double x = Double.parseDouble(coordsStr.substring(1, coordsStr.indexOf(",")).trim());
//            double y = Double.parseDouble(coordsStr.substring(coordsStr.indexOf(",") + 1, coordsStr.indexOf(")")).trim());
//            
//            int capacity = Integer.parseInt(parts[2].split("=")[1].trim().split(" ")[0]);
//
//            warehouses.add(new Warehouse(name, id, x, y, capacity));
//        } catch (Exception e) {
//            System.err.println("Error parsing warehouse line: " + line);
//        }
//    }

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