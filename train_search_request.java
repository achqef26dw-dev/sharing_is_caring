package requirement1;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Train {
    private final int train_id;
    private final Route train_route;
    private final LocalTime departure_time;
    private final LocalDate[] travelDate;
    private final String[][] seats = new String[100][4];
    private final boolean[][] occupied = new boolean[100][4];

    public Train( int train_id, Route train_route, LocalTime departure_time, LocalDate[] travelDate){
        this.train_id=train_id;
        this.train_route=train_route;
        this.departure_time=departure_time;
        this.travelDate= travelDate;
    }

    // A method that returns the position of the given seat
    private int[] findSeat(String seat) {
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[i].length; j++) {
                if (seats[i][j].equals(seat)) return new int[]{i, j};
            }
        }
        return null;
    }
    // Checks whether the seat is free
    public boolean isSeatFree(String seat) {
        int[] pos = findSeat(seat); // gets the position of the seat to check
        return pos != null && !occupied[pos[0]][pos[1]]; // true only if the seat exists and is not occupied
    }

    // This one reserves the seats and marks them as occupied
    public boolean reserveSeats(String[] requested) {
        for (String s : requested) {
            if (!isSeatFree(s)) return false;
        }
        for (String s : requested) {
            int[] pos = findSeat(s);
            occupied[pos[0]][pos[1]] = true;
        }
        return true;
    }

    // Basic getters
    public int getTrain_id() { return train_id; }
    public Route getRoute() { return train_route; }
    public LocalTime getDepartureTime() { return departure_time; }
    public LocalTime getArrivalTime(int station) {
        return departure_time.plusMinutes(train_route.getMinutes(0, station));
    }
    public LocalDate[] getTravelDate() {
        return travelDate;
    }
    // Adds to the train's departure time the time it takes to reach the arrival station




    //requirement 2
    private boolean matches
    (RenfeSystem sys, String from, String to, LocalDate date, LocalTime time){
        boolean validDate = false;
        for (int k = 0; k < travelDate.length && !validDate; k++) {
            if (travelDate[k].equals(date)) {
                validDate = true;
            }
        }
        if (!validDate){
            return false;
        }

        Station[] stations = this.train_route.getStations();
        int indexFrom = -1;
        int indexTo = -1;

        for (int j = 0; j < stations.length; j++) {
            if (stations[j].getName().equalsIgnoreCase(from) && this.getArrivalTime(j).equals(time)) {
                indexFrom = j;
            }
            if (stations[j].getName().equalsIgnoreCase(to)) {
                indexTo = j;
            }
        }

        return indexFrom != -1 && indexTo != -1 && indexTo>indexFrom;
    }

    private static Train search
        (RenfeSystem sys,String from, String to, LocalDate date, LocalTime time){
        for (int i = 0; i < sys.getTrainCount(); i++) {
            Train t = sys.getTrain(i);
            if (t.matches(sys, from, to, date, time)){
                return t;
            }
        }
        return null;
    }

    public static void searchRequest(RenfeSystem sys){
        Scanner sc = new Scanner(System.in);
        System.out.println("Insert the start station");
        String from = sc.next();
        System.out.println("Insert the final station");
        String to = sc.next();
        System.out.println("Insert date: (YYYY-MM-DD)");
        LocalDate date = LocalDate.parse(sc.next());
        System.out.println("Insert hour: (HH:MM)");
        LocalTime time = LocalTime.parse(sc.next());

        Train result = search(sys, from,to, date, time);
        if (result!=null){
            System.out.println("Found train: "+result.getTrain_id());
        } else {
            System.out.println("Train no found");
        }
    }



}
