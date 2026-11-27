/**
 * read this before starting
 * TrainSearch - Requirement 3: search trains.
 *
 * It prints the trains that leave from the origin to the destination
 * between 1 hour before and 1 hour after the wanted time (maximum 40
 * results, ordered by departure time).
 *
 * Times are stored as minutes since midnight (09:30 = 570).
 */
public class TrainSearch {

    final int MAX_RESULTS = 40;
    final int WINDOW = 60;
    final int LAST_MINUTE = 1439;

    // Train timetable: parallel arrays (the same position = the same train)
    String[] id          = {"T101", "T102", "T103", "T104", "T105", "T106", "T107", "T108", "T109", "T110", "T111", "T112"};
    String[] origin      = {"Madrid", "Madrid", "Madrid", "Madrid", "Madrid", "Barcelona", "Madrid", "Madrid", "Madrid", "Zaragoza", "Madrid", "Albacete"};
    String[] destination = {"Barcelona", "Barcelona", "Barcelona", "Valencia", "Barcelona", "Madrid", "Barcelona", "Barcelona", "Zaragoza", "Barcelona", "Albacete", "Valencia"};
    int[] departure      = {7 * 60 + 30, 8 * 60, 9 * 60 + 15, 9 * 60, 10 * 60, 9 * 60, 10 * 60 + 5, 12 * 60, 9 * 60 + 30, 11 * 60 + 30, 9 * 60 + 45, 12 * 60};
    int[] arrival        = {10 * 60 + 15, 10 * 60 + 40, 11 * 60 + 55, 11 * 60, 12 * 60 + 45, 11 * 60 + 45, 12 * 60 + 50, 14 * 60 + 40, 11 * 60, 13 * 60 + 15, 11 * 60 + 15, 13 * 60 + 40};


    private boolean isNumber(String text) {
        if (text.length() == 0) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public int toMinutes(String time) {
        if (time == null) {
            return -1;
        }
        String[] parts = time.trim().split(":");
        if (parts.length != 2 || !isNumber(parts[0].trim()) || !isNumber(parts[1].trim())) {
            return -1;
        }
        int hours = Integer.parseInt(parts[0].trim());
        int minutes = Integer.parseInt(parts[1].trim());
        if (hours > 23 || minutes > 59) {
            return -1;
        }
        return hours * 60 + minutes;
    }

    public String toText(int minutes) {
        String h = String.valueOf(minutes / 60);
        String m = String.valueOf(minutes % 60);
        if (h.length() == 1) h = "0" + h;
        if (m.length() == 1) m = "0" + m;
        return h + ":" + m;
    }



    private int[] routeTrains(String from, String to, int start, int end) {
        if (from == null || to == null) {
            return new int[0];
        }
        String a = from.trim();
        String b = to.trim();

        int[] found = new int[id.length];
        int count = 0;
        for (int i = 0; i < id.length; i++) {
            boolean sameRoute = origin[i].equalsIgnoreCase(a) && destination[i].equalsIgnoreCase(b);
            boolean inWindow = departure[i] >= start && departure[i] <= end;
            if (sameRoute && inWindow) {
                found[count] = i;
                count++;
            }
        }

        // Order by departure time (earliest first)
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (departure[found[j]] > departure[found[j + 1]]) {
                    int aux = found[j];
                    found[j] = found[j + 1];
                    found[j + 1] = aux;
                }
            }
        }

        int size = count;
        if (size > MAX_RESULTS) {
            size = MAX_RESULTS;
        }
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = found[i];
        }
        return result;
    }

    private void printTrain(int i) {
        System.out.println(id[i] + " | " + origin[i] + " -> " + destination[i]
                + " | departs " + toText(departure[i]) + " | arrives " + toText(arrival[i]));
    }

    // Requirement 3: prints the trains of a route within +-1 hour of the wanted time
    public void search(String from, String to, int wanted) {
        if (wanted < 0 || wanted > LAST_MINUTE) {
            System.out.println("Invalid time. Use HH:MM between 00:00 and 23:59.");
            return;
        }

        int start = Math.max(0, wanted - WINDOW);
        int end = Math.min(LAST_MINUTE, wanted + WINDOW);
        int[] found = routeTrains(from, to, start, end);

        if (found.length == 0) {
            System.out.println("No trains found between " + toText(start) + " and " + toText(end));
        } else {
            for (int i = 0; i < found.length; i++) {
                printTrain(found[i]);
            }
            System.out.println(found.length + " train(s) found.");
        }
    }

    // Prints all the trains of a route during the whole day
    public void searchAll(String from, String to) {
        int[] found = routeTrains(from, to, 0, LAST_MINUTE);

        if (found.length == 0) {
            System.out.println("No trains found for that route.");
        } else {
            for (int i = 0; i < found.length; i++) {
                printTrain(found[i]);
            }
            System.out.println(found.length + " train(s) found.");
        }
    }


    public boolean stationExists(String name) {
        if (name == null) {
            return false;
        }
        String station = name.trim();
        for (int i = 0; i < id.length; i++) {
            if (origin[i].equalsIgnoreCase(station) || destination[i].equalsIgnoreCase(station)) {
                return true;
            }
        }
        return false;
    }

    public int findIndex(String trainId) {
        if (trainId == null) {
            return -1;
        }
        for (int i = 0; i < id.length; i++) {
            if (id[i].equalsIgnoreCase(trainId.trim())) {
                return i;
            }
        }
        return -1;
    }
}
