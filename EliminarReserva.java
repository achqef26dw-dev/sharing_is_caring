package RETO_DE_RENFE.EliminarReserva_Ejercicio;
import java.util.Scanner;

public class EliminarReserva {

    private String[] reserva;
    private int reservasActuales;
    private String[] codigoReserva;

    public EliminarReserva(String[] reserva, int reservasActuales, String[] codigoReserva) {

        this.reserva = reserva;
        this.reservasActuales = reservasActuales;
        this.codigoReserva = codigoReserva;

    }


    public void eliminarReserva() {

        Scanner sc = new Scanner(System.in);

        System.out.println("Introduce el código de la reserva:");
        String codigoBuscado = sc.nextLine();

        int posicion = -1;

        for (int i = 0; i < reservasActuales; i++) {
            if (codigoReserva[i].equals(codigoBuscado)) {
                posicion = i;
            }
        }

        if (posicion == -1) {
            System.out.println("No se ha encontrado la reserva.");
        } else {

            System.out.println("Reserva encontrada:");
            System.out.println("Código: " + codigoReserva[posicion]);
            System.out.println(reserva[posicion]);

            System.out.println("¿Quieres eliminar esta reserva? (S/N)");
            String respuesta = sc.nextLine();

            if (respuesta.equalsIgnoreCase("S")) {

                for (int i = posicion; i < reservasActuales - 1; i++) {
                    reserva[i] = reserva[i + 1];
                    codigoReserva[i] = codigoReserva[i + 1];
                }

                reserva[reservasActuales - 1] = null;
                codigoReserva[reservasActuales - 1] = null;

                reservasActuales = reservasActuales - 1;

                System.out.println("Reserva eliminada correctamente.");

            } else {
                System.out.println("No se ha eliminado la reserva.");
            }
        }
    }
}