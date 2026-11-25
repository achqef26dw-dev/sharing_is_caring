import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TrainSearch {

    // ---------- Data structures ----------
    static class Stop {
        String station;
        int minutesToNext;                 // 0 for the last stop
        Stop(String station, int minutesToNext) {
            this.station = station;
            this.minutesToNext = minutesToNext;
        }
    }

    static class Route {
        String name;
        List<Stop> stops = new ArrayList<>();
        Route(String name) { this.name = name; }
        void addStop(String station, int minutesToNext) {
            stops.add(new Stop(station, minutesToNext));
        }
    }

    static class Train {
        int id;
        Route route;
        LocalTime departure;               // departure from the first station
        int seatRows;                      // 4 seats per row
        Train(int id, Route route, String departure, int seatRows) {
            this.id = id;
            this.route = route;
            this.departure = LocalTime.parse(departure);
            this.seatRows = seatRows;
        }
    }

    static class Result {
        Train train;
        LocalTime departs, arrives;
        double price;
        int freeSeats;
    }

    static List<Route> routes = new ArrayList<>();
    static List<Train> trains = new ArrayList<>();
    static Map<String, Double> prices = new HashMap<>();      // "Madrid>Barcelona" -> 45.0
    static Set<String> reservedSeats = new HashSet<>();       // "trainId|date|1A"

    // ---------- Sample data ----------
    static void addPrice(String a, String b, double price) {
        prices.put(a + ">" + b, price);
        prices.put(b + ">" + a, price);
    }

    static void loadData() {
        addPrice("Madrid", "Zaragoza", 25);
        addPrice("Madrid", "Barcelona", 45);
        addPrice("Zaragoza", "Barcelona", 20);
        addPrice("Madrid", "Valencia", 30);
        addPrice("Madrid", "Albacete", 20);
        addPrice("Albacete", "Valencia", 15);

        Route r1 = new Route("Madrid-Barcelona");
        r1.addStop("Madrid", 75);
        r1.addStop("Zaragoza", 90);
        r1.addStop("Barcelona", 0);

        Route r2 = new Route("Madrid-Valencia");
        r2.addStop("Madrid", 70);
        r2.addStop("Albacete", 80);
        r2.addStop("Valencia", 0);

        routes.add(r1);
        routes.add(r2);

        trains.add(new Train(1, r1, "08:00", 20));
        trains.add(new Train(2, r1, "14:30", 20));
        trains.add(new Train(3, r2, "09:15", 15));
        trains.add(new Train(4, r1, "08:30", 20));
        trains.add(new Train(5, r1, "18:00", 20));

        // two seats already reserved on train 1 (today + 3 days)
        LocalDate d = LocalDate.now().plusDays(3);
        reservedSeats.add("1|" + d + "|1A");
        reservedSeats.add("1|" + d + "|1B");
    }

    // ---------- Requirement 3: search ----------
    static int countReserved(int trainId, LocalDate date) {
        int count = 0;
        String prefix = trainId + "|" + date + "|";
        for (String s : reservedSeats) {
            if (s.startsWith(prefix)) count++;
        }
        return count;
    }

    static int minutesBefore(Route route, int stopIndex) {
        int sum = 0;
        for (int i = 0; i < stopIndex; i++) sum += route.stops.get(i).minutesToNext;
        return sum;
    }

    // time == null -> all trains of the day
    static List<Result> searchTrains(String from, String to, LocalDate date, LocalTime time) {
        List<Result> results = new ArrayList<>();

        for (Train t : trains) {
            int a = -1, b = -1;
            for (int i = 0; i < t.route.stops.size(); i++) {
                String st = t.route.stops.get(i).station;
                if (st.equalsIgnoreCase(from)) a = i;
                if (st.equalsIgnoreCase(to))   b = i;
            }
            if (a == -1 || b == -1 || a >= b) continue;        // wrong direction or not on route

            Double price = prices.get(t.route.stops.get(a).station + ">" + t.route.stops.get(b).station);
            if (price == null) continue;

            LocalTime departs = t.departure.plusMinutes(minutesBefore(t.route, a));
            LocalTime arrives = t.departure.plusMinutes(minutesBefore(t.route, b));

            if (time != null) {                                  // +-1 hour window
                int diff = Math.abs(departs.toSecondOfDay() - time.toSecondOfDay());
                if (diff > 3600) continue;
            }

            Result r = new Result();
            r.train = t;
            r.departs = departs;
            r.arrives = arrives;
            r.price = price;
            r.freeSeats = t.seatRows * 4 - countReserved(t.id, date);
            results.add(r);
        }

        results.sort(Comparator.comparing(r -> r.departs));
        if (results.size() > 40) results = results.subList(0, 40);   // maximum 40
        return results;
    }

    // ---------- Requirement 2: validation helper ----------
    static boolean stationExists(String name) {
        for (Route r : routes)
            for (Stop s : r.stops)
                if (s.station.equalsIgnoreCase(name)) return true;
        return false;
    }
}
