package gtfs;

import java.time.LocalTime;

public record StopTime(String tripId, LocalTime arrivalTime, String stopId) {

}
