package org.optaweb.tsp.optawebtspplanner.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.optaweb.tsp.optawebtspplanner.core.LatLng;
import org.optaweb.tsp.optawebtspplanner.core.Location;
import org.optaweb.tsp.optawebtspplanner.planner.RouteChangedEvent;
import org.optaweb.tsp.optawebtspplanner.routing.RoutingComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Broadcasts updated route to interested clients over WebSocket.
 */
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

    RouteMessage createResponse(String distanceString, List<Location> route) {
        List<List<PortableLocation>> segments = new ArrayList<>();
        for (int i = 1; i < route.size() + 1; i++) {
            // "trick" to get N -> 0 distance at the end of the loop
            Location fromLocation = route.get(i - 1);
            Location toLocation = route.get(i % route.size());
            List<LatLng> latLngs = routing.getRoute(fromLocation.getLatLng(), toLocation.getLatLng());
            segments.add(latLngs.stream()
                    .map(latLng -> new PortableLocation(0, latLng.getLatitude(), latLng.getLongitude()))
                    .collect(Collectors.toList())
            );
        }
        List<PortableLocation> networkingRoute = route.stream()
                .map(location -> new PortableLocation(
                        location.getId(),
                        location.getLatLng().getLatitude(),
                        location.getLatLng().getLongitude()))
                .collect(Collectors.toList());
        return new RouteMessage(distanceString, networkingRoute, segments);
    }
}
