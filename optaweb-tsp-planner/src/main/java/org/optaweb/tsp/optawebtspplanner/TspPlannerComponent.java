package org.optaweb.tsp.optawebtspplanner;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TspPlannerComponent {

    private final SimpMessagingTemplate webSocket;
    private final PlaceRepository repository;

    @Autowired
    public TspPlannerComponent(SimpMessagingTemplate webSocket,
                               PlaceRepository repository) {
        this.webSocket = webSocket;
        this.repository = repository;
    }

    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        if (repository.count() < 2) {
            return;
        }
        ArrayList<Place> places = new ArrayList<>();
        repository.findAll().iterator().forEachRemaining(places::add);
        Collections.shuffle(places);
        webSocket.convertAndSend("/topic/route", places);
    }
}
