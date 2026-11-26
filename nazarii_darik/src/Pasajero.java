import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Pasajero  {
    private final String DNI;
    private String nombre;
    private String apellido;
    private String email;
    private int tlf;
    private Date nacimiento;
    private List<Reserva> reservas;

    //constructor
    public Pasajero(String DNI, String nombre, String apellido, String email, int tlf, Date nacimiento){
        this.DNI = DNI;
        this.nombre=nombre;
        this.apellido=apellido;
        this.email=email;
        this.tlf=tlf;
        this.nacimiento=nacimiento;
        this.reservas = new ArrayList<>();
    }

    //getter and setter
    public int getViajesRealizados(){
        int cont=0;
        for(Reserva r:reservas){
            if(r.isRealizado()){
                cont++;
            }
        }
        return cont;
    }

    //methods
    public void anadirReserva(Reserva r){
        reservas.add(r);
        for(Asiento a: r.getAsientos()){
            a.ocuparAsiento();
        }
    }



    public double getDineroGastado(){
        double dinero=0;

        for(Reserva r:reservas){
            if(r.isRealizado()) {
                dinero += r.getPrecio();
            }
        }
        return dinero;
    }
}
