package requirement1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class train_search_request {

    public static RenfeSystem sys = new RenfeSystem();

    public static void createObjects() {
        Station madrid = new Station("Madrid");
        Station guadalajara = new Station("Guadalajara");
        Station zaragoza = new Station("Zaragoza");
        Station barcelona = new Station("Barcelona");

        Route madrid_barcelona = new Route(
                new double[][]{
                        {0, 10, 25, 45},
                        {10, 0, 18, 38},
                        {25, 18, 0, 20},
                        {45, 38, 20, 0}},
                new Station[]{madrid, guadalajara, zaragoza, barcelona},
                new int[]{30, 80, 90}
        );
        sys.addRoute(madrid_barcelona);

        // Varios trenes para comprobar que recorre toda la lista
        Train t1 = new Train(
                101, madrid_barcelona,
                LocalTime.of(8, 0),
                new LocalDate[]{LocalDate.of(2026, 12, 28), LocalDate.of(2026, 12, 30)}
        );
        sys.addTrain(t1);

        Train t2 = new Train(
                102, madrid_barcelona,
                LocalTime.of(15, 30),
                new LocalDate[]{LocalDate.of(2026, 12, 30), LocalDate.of(2026, 12, 31)}
        );
        sys.addTrain(t2);

        Passenger user1 = new Passenger(
                1, "Juanito", "Gonzalo",
                "juan@gmail.com", "juan1234"
        );
        sys.addPassenger(user1);

        Reservation user1R = new Reservation(
                1, user1, t1, madrid, barcelona,
                new String[]{"2A"}, LocalDate.of(2026, 12, 28)
        );
        sys.addReserve(user1R);

        user1R.getTrain().reserveSeats(user1R.getSeat());
    }



    public static Train search(String from, String to, LocalDate date, LocalTime time){
        for (int i = 0; i < sys.getTrainCount(); i++) {
            Train t = sys.getTrain(i);
            LocalDate[] dates = t.getTravelDate();
            boolean validDate = false;
            for (int k = 0; k < dates.length && !validDate; k++) {
                if (dates[k].equals(date)) {
                    validDate = true;
                }
            }


            if(validDate) {
                Route route = t.getRoute();
                Station[] stations = route.getStations();

                int indexFrom = -1;
                int indexTo = -1;

                for (int j = 0; j < stations.length; j++) {
                    if (stations[j].getName().equals(from) && t.getArrivalTime(j).equals(time)) {
                        indexFrom = j;
                    }
                    if (stations[j].getName().equals(to)) {
                        indexTo = j;
                    }
                }

                if (indexFrom != -1 && indexTo != -1 && indexTo>indexFrom) {
                    return t; // Devuelve el tren encontrado
                }
            }
        }

        return null;
    }

    public static void searchRequest(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Insert the start station");
        String from = sc.next();
        System.out.println("Insert the final station");
        String to = sc.next();
        System.out.println("Insert date: (YYYY-MM-DD)");
        LocalDate date = LocalDate.parse(sc.next());
        System.out.println("Insert hour: (HH:MM)");
        LocalTime time = LocalTime.parse(sc.next());

        Train result = search(from,to, date, time);
        if (result!=null){
            System.out.println("Found train: "+result.getTrain_id());
        } else {
            System.out.println("Train no found");
        }
    }

    public static void main(String[] args) {
        createObjects();
        searchRequest();
    }
}
