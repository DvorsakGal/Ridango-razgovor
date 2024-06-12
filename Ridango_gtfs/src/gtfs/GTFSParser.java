package gtfs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GTFSParser {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static Map<String, List<StopTime>> readStopTimes(String filepath) throws IOException {
        Map<String, List<StopTime>> stopTimes = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))){
            String line;
            reader.readLine();  //preskoci prvo vrstico
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String tripId = tokens[0];
                LocalTime arrivalTime = LocalTime.parse(tokens[1], TIME_FORMATTER); //bi znalo jebat
                String stopId = tokens[3];
                StopTime stopTime = new StopTime(tripId, arrivalTime, stopId);
                stopTimes.computeIfAbsent(stopId, k -> new ArrayList<>()).add(stopTime);
            }
        }
        return stopTimes;
    }

    public static Map<String, Trip> readTrips(String filepath) throws IOException {
        Map<String, Trip> trips = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String tripId = tokens[2];
                String routeId = tokens[0];
                Trip trip = new Trip(tripId, routeId);
                trips.put(tripId, trip);
            }
        }
        return trips;
    }

    public static Map<String, Route> readRoutes(String filepath) throws IOException {
        Map<String, Route> routes = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String routeId = tokens[0];
                String routeShortName = tokens[2];
                Route route = new Route(routeId, routeShortName);
                routes.put(routeId, route);
            }
        }
        return routes;
    }

}
