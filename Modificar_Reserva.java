import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Modificar_Reserva {
    public Reserva reserva;
    public Pasajero pasajero;
    public CodigoReserva codigo;
    public CompraBilletes billetes;

    public Modificar_Reserva(Reserva reserva, Pasajero pasajero, CodigoReserva codigo, CompraBilletes billetes) {
        this.reserva = reserva;
        this.pasajero = pasajero;
        this.codigo = codigo;
        this.billetes = billetes;
    }


    public void mostrarReserva() {
        this.reserva.mostrarReserva(this.pasajero);
    }

    public void modificarReserva(String codigoIntroducido) {
        Scanner sc = new Scanner(System.in);

        if (codigoIntroducido == null || codigoIntroducido.equals("")) {
            System.out.print("Introduce tu codigo de reserva: ");
            codigoIntroducido = sc.next();
        }

        if (codigoIntroducido.equals(this.codigo.getCodigo()) || codigoIntroducido.equals(this.reserva.getCOD())) {
            mostrarReserva();

            if (this.reserva.isRealizado()) {
                System.err.println("Error: Este viaje ya se ha realizado y no se puede modificar.");
            } else {
                System.out.println("\nViendo la informacion de esta reserva, ¿deseas hacer algun cambio? (SI/NO): ");
                String resp = sc.next();

                if (resp.equalsIgnoreCase("SI")) {
                    System.out.println("¿Que apartado deseas modificar?");
                    System.out.println("1. Comprobar disponibilidad en otra fecha");
                    System.out.println("2. Comprobar disponibilidad a otra hora");
                    System.out.println("3. Cambiar asientos / comprar nuevos billetes");
                    int opt = sc.nextInt();

                    switch (opt) {
                        case 1:
                            System.out.print("Introduce la nueva fecha (YYYY-MM-DD): ");
                            String textoFecha = sc.next();
                            LocalDate nuevaFecha = LocalDate.parse(textoFecha);

                            if (!nuevaFecha.isBefore(LocalDate.now())) {
                                TrainSearch.loadData();
                                System.out.print("Confirma estacion de origen: ");
                                String origen = sc.next();
                                System.out.print("Confirma estacion de destino: ");
                                String destino = sc.next();
                                if (TrainSearch.stationExists(origen) && TrainSearch.stationExists(destino)) {
                                    TrainSearch.searchTrains(origen, destino, nuevaFecha, null);
                                    System.out.println("Disponibilidad comprobada para el dia " + nuevaFecha + ".");
                                } else {
                                    System.err.println("Error: Alguna de las estaciones no existe.");
                                }
                            } else {
                                System.err.println("Error: No puedes elegir una fecha anterior a hoy.");
                            }
                            break;
                        case 2:
                            System.out.print("Introduce la nueva hora deseada (HH:MM): ");
                            String textoHora = sc.next();
                            LocalTime nuevaHora = LocalTime.parse(textoHora);

                            TrainSearch.loadData();
                            System.out.print("Confirma estacion de origen: ");
                            String orig = sc.next();
                            System.out.print("Confirma estacion de destino: ");
                            String dest = sc.next();

                            if (TrainSearch.stationExists(orig) && TrainSearch.stationExists(dest)) {
                                TrainSearch.searchTrains(orig, dest, LocalDate.now(), nuevaHora);
                                System.out.println("Horarios actualizados alrededor de las " + nuevaHora + ".");
                            } else {
                                System.err.println("Error: Estaciones no validas.");
                            }
                            break;
                        case 3:
                            int libres = this.reserva.mostrarAsientos(this.reserva.getTren());
                            System.out.println("Asientos libres actualmente en este tren: " + libres);

                            if (libres > 0) {
                                this.billetes = new CompraBilletes(this.reserva.getTren());
                                System.out.println("Proceso de cambio de asientos iniciado correctamente.");
                            } else {
                                System.err.println("Error: No quedan asientos libres en este tren.");
                            }
                            break;

                        default:
                            System.err.println("Error: Opcion no valida.");
                            break;
                    }
                } else if (resp.equalsIgnoreCase("NO")) {
                    System.out.println("No se han realizado cambios en la reserva.");
                } else {
                    System.err.println("Error: Debes responder SI o NO.");
                }
            }
        } else {
            System.err.println("Error: El codigo introducido no coincide con ninguna reserva.");
        }
    }
}