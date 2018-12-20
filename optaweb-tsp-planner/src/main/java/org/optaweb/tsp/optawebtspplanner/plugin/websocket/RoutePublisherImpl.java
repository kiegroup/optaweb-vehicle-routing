package org.optaweb.tsp.optawebtspplanner.plugin.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.optaweb.tsp.optawebtspplanner.domain.LatLng;
import org.optaweb.tsp.optawebtspplanner.interactor.route.Route;
import org.optaweb.tsp.optawebtspplanner.interactor.route.RoutePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Broadcasts updated route to interested clients over WebSocket.
 */
@Component
public class RoutePublisherImpl implements RoutePublisher {

    private final SimpMessagingTemplate webSocket;

    @Autowired
    public RoutePublisherImpl(SimpMessagingTemplate webSocket) {
        this.webSocket = webSocket;
    }

    @Override
    public void publish(Route route) {
        webSocket.convertAndSend("/topic/route", createResponse(route));
    }

    RouteMessage createResponse(Route route) {
        List<PortableLocation> portableRoute = route.getRoute().stream()
                .map(location -> new PortableLocation(
                        location.getId(),
                        location.getLatLng().getLatitude(),
                        location.getLatLng().getLongitude()))
                .collect(Collectors.toList());
        List<List<PortableLocation>> portableSegments = new ArrayList<>();
        for (List<LatLng> segment : route.getSegments()) {
            List<PortableLocation> portableSegment = segment.stream()
                    .map(latLng -> new PortableLocation(0, latLng.getLatitude(), latLng.getLongitude()))
                    .collect(Collectors.toList());
            portableSegments.add(portableSegment);
        }
        return new RouteMessage(route.getDistance(), portableRoute, portableSegments);
    }
}
