package com.example.betnetix.handler;

import com.example.betnetix.model.RunnerParams;
import com.example.betnetix.service.RaceService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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


    private final Map<Long, RunnerParams> runnerParams = new HashMap<>();
    private final Map<Long, Double> positions = new ConcurrentHashMap<>();
    private final List<Long> finalPositions = Collections.synchronizedList(new ArrayList<>());
    private final double totalDistance = 100.0;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HashMap<Integer, Long> places = new HashMap<>();


    public RaceSimulationWebSocketHandler() {
        // Инициализация параметров бегунов
        runnerParams.put(1L, new RunnerParams(10, 0.5));
        runnerParams.put(2L, new RunnerParams(10, 0.5));
        runnerParams.put(3L, new RunnerParams(10, 0.5));
        runnerParams.put(4L, new RunnerParams(10, 0.5));
        runnerParams.put(5L, new RunnerParams(10, 0.5));
        runnerParams.put(6L, new RunnerParams(10, 0.5));

        // Инициализация начальных позиций
        runnerParams.keySet().forEach(runner -> positions.put(runner, 0.0));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        startSimulation(session);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        try {

            Map<Long, List<Double>> paramsMap = objectMapper.readValue(
                    message.getPayload(),
                    new TypeReference<>() {
                    }
            );


            runnerParams.clear();
            paramsMap.forEach((runnerId, params) -> {
                if (params.size() >= 2) {
                    runnerParams.put(runnerId, new RunnerParams(params.get(0), params.get(1)));
                }
            });


            positions.clear();
            runnerParams.keySet().forEach(runner -> positions.put(runner, 0.0));

            session.sendMessage(new TextMessage("{\"status\":\"Parameters updated\"}"));

        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"error\":\"Invalid data format\"}"));
        }
    }

    private void startSimulation(WebSocketSession session) throws Exception {
        int timeElapsed = 0;
        Random random = new Random();

        List<Long> shuffledRunnerIds = new ArrayList<>(runnerParams.keySet());
        Collections.shuffle(shuffledRunnerIds);

        Map<String, Object> data = new HashMap<>();
        data.put("second", timeElapsed);
        data.put("positions", new HashMap<>(positions));
        data.put("places", new HashMap<>(places));

        // Отправка через WebSocket
        String jsonData = objectMapper.writeValueAsString(data);
        session.sendMessage(new TextMessage(jsonData));
        System.out.println(jsonData);

        Thread.sleep(1000);

        while (positions.values().stream().anyMatch(pos -> pos < totalDistance)) {
            timeElapsed++;
            Map<String, Double> currentSpeeds = new HashMap<>();

            // Расчет скоростей и обновление позиций
            for (Long runner : shuffledRunnerIds) {
                System.out.println(runnerParams.get(runner).toString());
                if (positions.get(runner) < totalDistance) {
                    RunnerParams params = runnerParams.get(runner);
                    // Генерация нормального распределения
                    double speed = params.mu() + params.sigma() * random.nextGaussian();
                    double newPosition = Math.min(positions.get(runner) + speed, totalDistance);
                    positions.put(runner, Math.round(newPosition * 100.0) / 100.0);


                    if (positions.get(runner) == totalDistance && !finalPositions.contains(runner)) {
                        finalPositions.add(runner);
                        System.out.println("Человек добежал: " + runner);
                    }
                }
            }
            recountPlaces();
            // Подготовка данных для отправки
            data = new HashMap<>();
            data.put("second", timeElapsed);
            data.put("positions", new HashMap<>(positions));
            data.put("places", new HashMap<>(places));

            // Отправка через WebSocket
            jsonData = objectMapper.writeValueAsString(data);
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

    private void recountPlaces() {

        places.clear();

        List<Long> stillRunning = positions.entrySet().stream()
                .filter(entry -> entry.getValue() < 100)
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();


        for (int i = 0; i < finalPositions.size(); i++) {
            places.put(i + 1, finalPositions.get(i));
        }
        for (int i = 0; i < stillRunning.size(); i++) {
            places.put(i + finalPositions.size() + 1, stillRunning.get(i));
        }

    }
}