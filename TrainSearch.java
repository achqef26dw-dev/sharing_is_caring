public class TrainSearch {

    // ---------- CLASSES ----------
    static class Route {
        String name;
        int[] stops;        // station indexes
        int[] minutes;      // minutes from each stop to the next (0 for the last)

        Route(String name, int[] stops, int[] minutes) {
            this.name = name;
            this.stops = stops;
            this.minutes = minutes;
        }
    }

    static class Train {
        int id;
        Route route;
        int departure;      // minutes since 00:00 (08:00 = 480)
        int seatRows;       // 4 seats per row

        Train(int id, Route route, int departure, int seatRows) {
            this.id = id;
            this.route = route;
            this.departure = departure;
            this.seatRows = seatRows;
        }
    }

    static class Result {
        Train train;
        int departs, arrives;
        double price;
        int freeSeats;
    }

    // ---------- DATA (arrays and matrices) ----------
    static String[] stations = {"Madrid", "Zaragoza", "Barcelona", "Valencia", "Albacete"};
    static double[][] prices = new double[5][5];     // 0 = no price
    static Route[] routes = new Route[2];
    static Train[] trains = new Train[5];
    // each row: {trainId, dayOffset (0 = today), seatNumber}
    static int[][] reserved = { {1, 3, 1}, {1, 3, 2} };

    static void setPrice(int a, int b, double price) {
        prices[a][b] = price;
        prices[b][a] = price;
    }

    static void loadData() {
        setPrice(0, 1, 25);  // Madrid - Zaragoza
        setPrice(0, 2, 45);  // Madrid - Barcelona
        setPrice(1, 2, 20);  // Zaragoza - Barcelona
        setPrice(0, 3, 30);  // Madrid - Valencia
        setPrice(0, 4, 20);  // Madrid - Albacete
        setPrice(4, 3, 15);  // Albacete - Valencia

        routes[0] = new Route("Madrid-Barcelona", new int[]{0, 1, 2}, new int[]{75, 90, 0});
        routes[1] = new Route("Madrid-Valencia",  new int[]{0, 4, 3}, new int[]{70, 80, 0});

        trains[0] = new Train(1, routes[0], 480, 20);    // 08:00
        trains[1] = new Train(2, routes[0], 870, 20);    // 14:30
        trains[2] = new Train(3, routes[1], 555, 15);    // 09:15
        trains[3] = new Train(4, routes[0], 510, 20);    // 08:30
        trains[4] = new Train(5, routes[0], 1080, 20);   // 18:00
    }

    // ---------- HELPER FUNCTIONS ----------
    static int stationIndex(String name) {
        for (int i = 0; i < stations.length; i++) {
            if (stations[i].equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    static int positionInRoute(Route route, int station) {
        for (int i = 0; i < route.stops.length; i++) {
            if (route.stops[i] == station) return i;
        }
        return -1;
    }

    static int minutesBefore(Route route, int position) {
        int sum = 0;
        for (int i = 0; i < position; i++) sum += route.minutes[i];
        return sum;
    }

    static int countReserved(int trainId, int dayOffset) {
        int count = 0;
        for (int i = 0; i < reserved.length; i++) {
            if (reserved[i][0] == trainId && reserved[i][1] == dayOffset) count++;
        }
        return count;
    }

    static String formatTime(int minutes) {
        int h = (minutes / 60) % 24;
        int m = minutes % 60;
        return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m;
    }

    // ---------- SEARCH ----------
    // time = -1 -> all trains of the day
    static Result[] searchTrains(String from, String to, int dayOffset, int time) {
        int fromIdx = stationIndex(from);
        int toIdx = stationIndex(to);
        if (fromIdx == -1 || toIdx == -1) return new Result[0];

        Result[] temp = new Result[trains.length];
        int count = 0;

        for (int t = 0; t < trains.length; t++) {
            Route route = trains[t].route;
            int a = positionInRoute(route, fromIdx);
            int b = positionInRoute(route, toIdx);

            if (a == -1 || b == -1 || a >= b) continue;     // wrong direction / not on route
            if (prices[fromIdx][toIdx] == 0) continue;      // no price

            int departs = trains[t].departure + minutesBefore(route, a);
            int arrives = trains[t].departure + minutesBefore(route, b);

            if (time != -1 && Math.abs(departs - time) > 60) continue;   // +-1 hour

            Result r = new Result();
            r.train = trains[t];
            r.departs = departs;
            r.arrives = arrives;
            r.price = prices[fromIdx][toIdx];
            r.freeSeats = trains[t].seatRows * 4 - countReserved(trains[t].id, dayOffset);
            temp[count] = r;
            count++;
        }

        // bubble sort by departure time
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (temp[j].departs > temp[j + 1].departs) {
                    Result aux = temp[j];
                    temp[j] = temp[j + 1];
                    temp[j + 1] = aux;
                }
            }
        }

        if (count > 40) count = 40;                         // maximum 40

        Result[] results = new Result[count];
        for (int i = 0; i < count; i++) results[i] = temp[i];
        return results;
    }

    static void printResults(Result[] results) {
        if (results.length == 0) {
            System.out.println("No trains found.");
            return;
        }
        for (int i = 0; i < results.length; i++) {
            Result r = results[i];
            System.out.println("Train " + r.train.id + " | " + formatTime(r.departs)
                    + " -> " + formatTime(r.arrives) + " | " + r.price
                    + " EUR | free seats: " + r.freeSeats);
        }
    }

    // ---------- TEST ----------
    public static void main(String[] args) {
        loadData();

        System.out.println("All trains Madrid -> Barcelona (in 3 days):");
        printResults(searchTrains("Madrid", "Barcelona", 3, -1));

        System.out.println();
        System.out.println("Around 08:00, Madrid -> Zaragoza:");
        printResults(searchTrains("Madrid", "Zaragoza", 0, 480));

        System.out.println();
        System.out.println("Wrong direction:");
        printResults(searchTrains("Barcelona", "Madrid", 0, -1));
    }
}
