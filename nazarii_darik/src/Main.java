import java.sql.Time;
import java.util.*;

public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    System.out.println("========= MENU ==========");
    System.out.println("\n1. Buscar Tren");
    System.out.println("2. Comprar Billete");
    System.out.println("3. Modificar Reserva");
    System.out.println("4. Eliminar Reserva");
    System.out.println("5. Salir");

    int num = sc.nextInt();

    while(num!=5){
        switch (num){
            case 1:
                //Tren.buscarTren();
                break;
            case 2:
                //Reserva.comprarBillete();
                break;
            case 3:
                //Reserva.modificarReserva();
                break;
            case 4:
                //Reserva.eliminarReserva();
                break;
            default:
                System.out.println("Numero no valido, introduce una opcion del menu");
                num = sc.nextInt();
                break;
        }
        num=sc.nextInt();
    }
}
