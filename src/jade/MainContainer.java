package jade;

import jade.agents.AverageAgent;
import jade.agents.GraphTopology;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class MainContainer {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        // Создаем профиль для главного контейнера
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1098");
        profile.setParameter(Profile.GUI, "true"); // Включить GUI

        // Создаем главный контейнер
        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            System.out.println("Создаём и запускаем всех агентов для расчета среднего арифметического");
            System.out.println("Граф связей:");
            for (int i = 1; i <= 7; i++) {
                System.out.println("   Агент " + i + " (" + GraphTopology.getAgentValue(i) +
                        ") связан с: " + GraphTopology.getNeighbors(i));
            }
            System.out.println();

            // Создаем и запускаем 7 агентов
            for (int i = 1; i <= 7; i++) {
                AgentController agent = mainContainer.createNewAgent(
                        "agent" + i,
                        AverageAgent.class.getName(),
                        new Object[]{} // Аргументы для агента
                );
                agent.start();
            }

            System.out.println("Обмен данными...");

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
