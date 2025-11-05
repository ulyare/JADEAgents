package jade.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 * Основной класс агента для расчета среднего арифметического
 */
public class AverageAgent extends Agent {
    private AgentData agentData;
    private Set<Integer> receivedFrom = new HashSet<>();
    private int currentStage = 0;
    private boolean isFirstTick = true;
    private static int agentsReadyForStage = 0;
    private static final Object lock = new Object();
    private static int agentsThatKnowAll = 0;
    private static boolean allAgentsShouldStop = false;

    @Override
    protected void setup() {
        String agentName = getLocalName();
        int agentId = Integer.parseInt(agentName.replace("agent", ""));
        double value = GraphTopology.getAgentValue(agentId);

        agentData = new AgentData(agentId, value);

        // Добавляем поведение для приема сообщений
        addBehaviour(new ReceiveMessagesBehaviour());

        // Запускаем периодическую отправку сообщений
        addBehaviour(new ExchangeDataBehaviour(this, 5000));
    }

    /**
     * Поведение для отправки данных соседям
     */
    private class ExchangeDataBehaviour extends TickerBehaviour {
        public ExchangeDataBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            // Проверяем, нужно ли остановиться
            if (allAgentsShouldStop) {
                stop();
                return;
            }

            if (isFirstTick) {
                isFirstTick = false;
                return;
            }

            currentStage++;
            receivedFrom.clear();

            // Отправляем данные
            sendDataToNeighbors();

            // Ждем получения всех сообщений
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Синхронизированный вывод результатов этапа
            synchronized (lock) {
                agentsReadyForStage++;

                // Первый агент выводит заголовок этапа
                if (agentsReadyForStage == 1) {
                    System.out.println("\n════════════════════════════════════════");
                    System.out.println("ЭТАП " + currentStage);
                }

                // Выводим результат этого агента
                System.out.println("Агент " + agentData.getAgentId() +
                        " | Знает: " + agentData.getKnownAgents().size() + " агентов");

                // Считаем сколько агентов знают всех
                if (agentData.knowsAllAgents()) {
                    agentsThatKnowAll++;
                }

                // Последний агент завершает вывод этапа
                if (agentsReadyForStage == 7) {
                    agentsReadyForStage = 0;

                    // Если все агенты знают всех - выводим финальные результаты
                    if (agentsThatKnowAll == 7) {
                        System.out.println("\nВСЕ ЗНАЮТ ОБО ВСЕХ");
                        System.out.println("ФИНАЛЬНЫЕ РЕЗУЛЬТАТЫ:");
                        double avg = GraphTopology.calculateRealAverage();
                        for (int i = 1; i <= 7; i++) {
                            System.out.println("Агент " + i + " вычислил среднее: " + String.format("%.2f", avg));
                        }
                        System.out.println("════════════════════════════════════════");
                        System.out.println("Реальное среднее арифметическое: " + String.format("%.2f", avg));

                        // Устанавливаем флаг остановки для всех агентов
                        allAgentsShouldStop = true;
                    } else {
                        // Сбрасываем счетчик для следующего этапа
                        agentsThatKnowAll = 0;
                    }
                }
            }
        }

        private void sendDataToNeighbors() {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

            try {
                msg.setContentObject(agentData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int neighborId : GraphTopology.getNeighbors(agentData.getAgentId())) {
                msg.addReceiver(new jade.core.AID("agent" + neighborId, jade.core.AID.ISLOCALNAME));
            }

            send(msg);
        }
    }

    /**
     * Поведение для приема сообщений
     */
    private class ReceiveMessagesBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                try {
                    AgentData receivedData = (AgentData) msg.getContentObject();
                    int senderId = receivedData.getAgentId();

                    receivedFrom.add(senderId);
                    agentData.mergeKnowledge(receivedData.getKnownAgents());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }
    }

    public AgentData getAgentData() {
        return agentData;
    }
}