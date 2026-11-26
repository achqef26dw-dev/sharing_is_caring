import java.time.LocalDate;
import java.util.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Reserva {
    private final String COD;
    private Tren tren;
    private Recorrido salida;
    private Recorrido llegada;
    private LocalTime horaSalida;
    private LocalTime horaLlegada;
    private int duracion; // en minutos
    private List<Asiento> asientos;
    private double precio;
    private boolean realizado;

    //contructor
    public Reserva(Tren tren, Recorrido salida, Recorrido llegada, LocalTime horaSalida, LocalTime horaLlegada) {
        this.COD = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.tren = tren;
        this.salida = salida;
        this.llegada = llegada;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;

        Duration duracion = Duration.between(horaSalida, horaLlegada);
        if (duracion.isNegative()) duracion = duracion.plusHours(24);
        this.duracion = (int) duracion.toMinutes();

        this.asientos = new ArrayList<>();
        this.precio = 0;
        this.realizado = false;
    }

    //getter and setter
    public double getPrecio(){
        return precio;
    }
    public Tren getTren() {
        return tren;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }
    public String getCOD(){
        return COD;
    }
    public void setRealizado(boolean realizado){
        this.realizado = realizado;
    }
    public String getCod() {
        return COD;
    }
    public List<Asiento> getAsientos(){
        if(!realizado){
            return asientos;
        }
        return Collections.emptyList();
    }

    //methods
    public void anadirAsiento(Asiento a) {
        asientos.add(a);
        precio += a.getPrecio();
    }

    public boolean isRealizado(){
        return realizado;
    }

    public void mostrarReserva(Pasajero p) {
        System.out.println("CODIGO DE RESERVA: " + COD);
        System.out.println("----------------------");
        System.out.println("Tren: " + tren);
        System.out.println("Salida: " + salida);
        System.out.println("Destino: " + llegada);
        System.out.println("Hora de salida: " + horaSalida);
        System.out.println("Hora de llegada: " + horaLlegada);
        System.out.println("Duracion: " + (duracion / 60) + "h " + (duracion % 60) + "min");
        System.out.println("Asientos seleccionados: ");
        for (Asiento a : asientos) {
            System.out.print(a.getModelo()+" ");
        }
        System.out.println("Precio: " + precio + " €");
        if(p.getViajesRealizados()>0) {
            System.out.println("\nEn total tienes " + p.getViajesRealizados() + " reservas y has gastado " + p.getDineroGastado() + "€");
        }else{
            System.out.println("Todavia no tienes ningun viaje realizado");
        }
    }

    //mejoras
    public int mostrarAsientos(Tren t){
        int count=0;
        List<Asiento> libres = new ArrayList<>();
        libres = t.asientosLibres();
        for(Asiento a: libres){
            count++;
        }
        return count;
    }

    public String preferencias(int num){
        switch (num){
            case 1: return "ventana";
            case 2: return "pasillo";
            case 3: return "mesa";
            case 4: return "tranquila";
            case 5: return "salida";
            default: return "";
        }
    }
    //mejora IA


    public String generarIcs() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        ZoneId zona = ZoneId.of("Europe/Madrid");

        LocalDateTime inicio = LocalDateTime.of(tren.getFecha(), horaSalida);
        LocalDateTime fin = inicio.plusMinutes(duracion);

        String dtStart = inicio.atZone(zona).withZoneSameInstant(ZoneOffset.UTC).format(fmt);
        String dtEnd = fin.atZone(zona).withZoneSameInstant(ZoneOffset.UTC).format(fmt);
        String ahora = LocalDateTime.now().atZone(zona).withZoneSameInstant(ZoneOffset.UTC).format(fmt);

        String origen = tren.getRecorrido().getEstacionIni().getNombre();
        String destino = tren.getRecorrido().getEstacionFin().getNombre();

        StringBuilder sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("PRODID:-//MiApp Trenes//ES\r\n");
        sb.append("BEGIN:VEVENT\r\n");
        sb.append("UID:").append(COD).append("@miapptrenes\r\n");
        sb.append("DTSTAMP:").append(ahora).append("\r\n");
        sb.append("DTSTART:").append(dtStart).append("\r\n");
        sb.append("DTEND:").append(dtEnd).append("\r\n");
        sb.append("SUMMARY:Tren ").append(origen).append(" - ").append(destino).append("\r\n");
        sb.append("LOCATION:").append(origen).append("\r\n");
        sb.append("DESCRIPTION:Codigo de reserva: ").append(COD)
                .append("\\nPrecio: ").append(precio).append(" EUR\r\n");
        sb.append("END:VEVENT\r\n");
        sb.append("END:VCALENDAR\r\n");
        return sb.toString();
    }
}