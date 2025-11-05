package jade.agents;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для хранения данных агента
 */
public class AgentData implements Serializable {
    private int agentId;
    private double value;
    private Map<Integer, Double> knownAgents; // ID агента -> его значение

    public AgentData(int agentId, double value) {
        this.agentId = agentId;
        this.value = value;
        this.knownAgents = new HashMap<>();
        // Агент изначально знает только о себе
        this.knownAgents.put(agentId, value);
    }

    public int getAgentId() { return agentId; }
    public double getValue() { return value; }
    public Map<Integer, Double> getKnownAgents() { return knownAgents; }

    /**
     * Добавление данных о другом агенте
     */
    public void addAgentData(int id, double value) {
        knownAgents.put(id, value);
    }

    /**
     * Объединение знания с другим агентом
     */
    public void mergeKnowledge(Map<Integer, Double> otherKnowledge) {
        knownAgents.putAll(otherKnowledge);
    }

    /**
     * Проверка, знает ли агент о всех 7 агентах
     */
    public boolean knowsAllAgents() {
        return knownAgents.size() == 7;
    }

    /**
     * Вычисление среднего арифметического всех известных значений
     */
    public double calculateAverage() {
        if (knownAgents.isEmpty()) return 0;

        double sum = 0;
        for (double val : knownAgents.values()) {
            sum += val;
        }
        return sum / knownAgents.size();
    }
}