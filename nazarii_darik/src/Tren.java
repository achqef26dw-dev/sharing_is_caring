import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tren {
    private LocalDate fecha;
    private LocalTime horaSalida;
    private Recorrido r;
    private ArrayList<Asiento> asientos = new ArrayList<>();

    // Todos los trenes del sistema
    private static List<Tren> trenes = new ArrayList<>();

    // constructor
    public Tren(LocalDate fecha, LocalTime horaSalida, Recorrido r) {
        this.fecha = fecha;
        this.horaSalida = horaSalida;
        this.r = r;
        trenes.add(this);
    }

    // getter and setter
    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public Recorrido getRecorrido() {
        return r;
    }

    public ArrayList<Asiento> getAsientos() {
        return asientos;
    }

    // methods
    public ArrayList<Asiento> asientosLibres() {
        ArrayList<Asiento> libres = new ArrayList<>();
        for (Asiento a : asientos) {
            if (a.getDispo()) {
                libres.add(a);
            }
        }
        return libres;
    }

    public double precioMinimo() {
        double min = -1;
        for (Asiento a : asientosLibres()) {
            if (min == -1 || a.getPrecio() < min) {
                min = a.getPrecio();
            }
        }
        return min;
    }

    public static double precioMinimoDia(Estacion ini, Estacion fin, LocalDate dia) {
        double min = -1;
        for (Tren t : trenes) {
            boolean mismoDia = t.fecha.equals(dia);
            boolean mismoTrayecto = t.r.getEstacionIni().getNombre().equals(ini.getNombre())
                    && t.r.getEstacionFin().getNombre().equals(fin.getNombre());
            if (mismoDia && mismoTrayecto) {
                double p = t.precioMinimo();
                if (p != -1 && (min == -1 || p < min)) {
                    min = p;
                }
            }
        }
        return min;
    }

    public static Map<LocalDate, Double> preciosCercanos(Estacion ini, Estacion fin, LocalDate fechaElegida) {
        Map<LocalDate, Double> precios = new LinkedHashMap<>();

        for (int i = -3; i <= 3; i++) {
            LocalDate dia = fechaElegida.plusDays(i);
            double precio = precioMinimoDia(ini, fin, dia);
            precios.put(dia, precio == -1 ? null : precio);
        }
        return precios;
    }
}