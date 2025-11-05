package jade.agents;


import java.util.*;

/**
 * Класс определяет граф связей между агентами
 */
public class GraphTopology {
    private static final Map<Integer, List<Integer>> connections = new HashMap<>();
    private static final Map<Integer, Double> agentValues = new HashMap<>();

    static {
        // Связи между агентами
        connections.put(1, Arrays.asList(2, 5, 7));
        connections.put(2, Arrays.asList(1, 3));
        connections.put(3, Arrays.asList(2, 4));
        connections.put(4, Arrays.asList(3, 5, 6));
        connections.put(5, Arrays.asList(1, 4, 7));
        connections.put(6, Arrays.asList(4, 7));
        connections.put(7, Arrays.asList(1, 5, 6));

        // Значения агентов
        agentValues.put(1, 35.0);
        agentValues.put(2, -7.0);
        agentValues.put(3, 5.0);
        agentValues.put(4, 34.0);
        agentValues.put(5, 7.0);
        agentValues.put(6, 80.0);
        agentValues.put(7, 11.0);
    }

    public static List<Integer> getNeighbors(int agentId) {
        return connections.getOrDefault(agentId, new ArrayList<>());
    }

    public static double getAgentValue(int agentId) {
        return agentValues.get(agentId);
    }

    public static Set<Integer> getAllAgentIds() {
        return agentValues.keySet();
    }

    /**
     * Реальное среднее арифметическое для сравнения
     */
    public static double calculateRealAverage() {
        double sum = 0;
        for (double value : agentValues.values()) {
            sum += value;
        }
        return sum / agentValues.size();
    }
}
