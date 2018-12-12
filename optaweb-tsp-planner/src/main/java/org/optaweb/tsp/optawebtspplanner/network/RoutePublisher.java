package org.optaweb.tsp.optawebtspplanner.network;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.optaweb.tsp.optawebtspplanner.RouteChangedEvent;
import org.optaweb.tsp.optawebtspplanner.RoutingComponent;
import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RoutePublisher implements ApplicationListener<RouteChangedEvent> {

    private final SimpMessagingTemplate webSocket;
    private final RoutingComponent routing;

    @Autowired
    public RoutePublisher(SimpMessagingTemplate webSocket, RoutingComponent routing) {
        this.webSocket = webSocket;
        this.routing = routing;
    }

    @Override
    public void onApplicationEvent(RouteChangedEvent event) {
        // TODO persist the best solution (here?)
        webSocket.convertAndSend("/topic/route", createResponse(event.getDistance(), event.getRoute()));
    }

    RouteMessage createResponse(String distanceString, List<Place> route) {
        List<List<Place>> segments = new ArrayList<>();
        for (int i = 1; i < route.size() + 1; i++) {
            // "trick" to get N -> 0 distance at the end of the loop
            Place fromPlace = route.get(i - 1);
            Place toPlace = route.get(i % route.size());
            LatLng fromLatLng = new LatLng(fromPlace.getLatitude(), fromPlace.getLongitude());
            LatLng toLatLng = new LatLng(toPlace.getLatitude(), toPlace.getLongitude());
            List<LatLng> latLngs = routing.getRoute(fromLatLng, toLatLng);
            segments.add(latLngs.stream()
                    .map(latLng -> new Place(0, latLng.getLatitude(), latLng.getLongitude()))
                    .collect(Collectors.toList())
            );
        }
        return new RouteMessage(distanceString, route, segments);
    }
}
