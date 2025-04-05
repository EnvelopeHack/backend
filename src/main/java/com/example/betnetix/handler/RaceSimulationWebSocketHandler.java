package com.example.betnetix.handler;

import com.example.betnetix.service.RaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RaceSimulationWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private RaceService raceService;

    @Getter
    private static class RunnerParams {
        private double mu;
        private double sigma;

        public RunnerParams(double mu, double sigma) {
            this.mu = mu;
            this.sigma = sigma;
        }

    }

    private final Map<Long, RunnerParams> studentParams = new HashMap<>();
    private final Map<Long, Double> positions = new ConcurrentHashMap<>();
    private final List<Long> finalPositions = Collections.synchronizedList(new ArrayList<>());
    private final double totalDistance = 100.0;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RaceSimulationWebSocketHandler() {
        // Инициализация параметров бегунов
        studentParams.put(1L, new RunnerParams(10, 0.5));
        studentParams.put(2L, new RunnerParams(10, 0.5));
        studentParams.put(3L, new RunnerParams(10, 0.5));
        studentParams.put(4L, new RunnerParams(10, 0.5));
        studentParams.put(5L, new RunnerParams(10, 0.5));
        studentParams.put(6L, new RunnerParams(10, 0.5));

        // Инициализация начальных позиций
        studentParams.keySet().forEach(student -> positions.put(student, 0.0));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        startSimulation(session);
    }

    private void startSimulation(WebSocketSession session) throws Exception {
        int timeElapsed = 0;
        Random random = new Random();

        while (positions.values().stream().anyMatch(pos -> pos < totalDistance)) {
            timeElapsed++;
            Map<String, Double> currentSpeeds = new HashMap<>();

            // Расчет скоростей и обновление позиций
            for (Long student : studentParams.keySet()) {
                if (positions.get(student) < totalDistance) {
                    RunnerParams params = studentParams.get(student);
                    // Генерация нормального распределения
                    double speed = params.getMu() + params.getSigma() * random.nextGaussian();
                    double newPosition = Math.min(positions.get(student) + speed, totalDistance);
                    positions.put(student, Math.round(newPosition * 100.0) / 100.0);

                    if (positions.get(student) == totalDistance && !finalPositions.contains(student)) {
                        finalPositions.add(student);
                        System.out.println("Человек добежал: " + student);
                    }
                }
            }

            // Подготовка данных для отправки
            Map<String, Object> data = new HashMap<>();
            data.put("second", timeElapsed);
            data.put("positions", new HashMap<>(positions));

            // Отправка через WebSocket
            String jsonData = objectMapper.writeValueAsString(data);
            session.sendMessage(new TextMessage(jsonData));
            System.out.println(jsonData);

            Thread.sleep(1000); // Задержка 1 секунда
        }
        // Вызываем метод для обновления рейсов. (внутри него ивызывется метод для обновлений вероятностей для )
        System.out.println(finalPositions);
        raceService.addRaceStat(finalPositions);

        positions.keySet().forEach(student -> positions.put(student, 0.0));
        finalPositions.clear();

        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("WebSocket соединение закрыто: " + status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Ошибка WebSocket: " + exception.getMessage());
    }
}