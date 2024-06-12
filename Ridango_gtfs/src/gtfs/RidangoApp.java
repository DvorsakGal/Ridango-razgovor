package gtfs;

import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class RidangoApp {
    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Usage: java BusScheduleApp <stop_id> <number_of_buses> <relative|absolute>");
            return;
        }

        String stopId = args[0];
        int numberOfBuses = Integer.parseInt(args[1]);
        boolean relative = "relative".equalsIgnoreCase(args[2]);

        try {
            Map<String, List<StopTime>> stopTimes = GTFSParser.readStopTimes("gtfs_files/stop_times.txt");
            Map<String, Trip> trips = GTFSParser.readTrips("gtfs_files/trips.txt");
            Map<String, Route> routes = GTFSParser.readRoutes("gtfs_files/routes.txt");

            LocalTime now = LocalTime.now();
            LocalTime twoHoursLater = now.plusHours(2);

            List<StopTime> arrivals = stopTimes.getOrDefault(stopId, Collections.emptyList()).stream()
                    .filter(stopTime -> stopTime.arrivalTime().isAfter(now) && stopTime.arrivalTime().isBefore(twoHoursLater))
                    .toList();

            Map<String, List<LocalTime>> routeArrivals = new HashMap<>();
            for (StopTime stopTime : arrivals) {
                Trip trip = trips.get(stopTime.tripId());
                if (trip != null) {
                    Route route = routes.get(trip.routeId());
                    if (route != null) {
                        routeArrivals.computeIfAbsent(route.routeShortName(), k -> new ArrayList<>()).add(stopTime.arrivalTime());
                    }
                }
            }

            for (Map.Entry<String, List<LocalTime>> entry : routeArrivals.entrySet()) {
                String routeShortName = entry.getKey();
                List<LocalTime> times = entry.getValue().stream()
                        .sorted().limit(numberOfBuses).toList();

                String timeString = times.stream().map(time -> relative ? now.until(time, ChronoUnit.MINUTES) + " minutes" : time.toString())
                        .collect(Collectors.joining(", "));

                System.out.println(routeShortName + ": " + timeString);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}