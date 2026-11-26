public class Recorrido {
    private Estacion estacionIni;
    private Estacion estacionFin;
    private int tiempo;

    //constructor
    public Recorrido(Estacion estacionIni, Estacion estacionFin, int tiempo){
        this.estacionIni=estacionIni;
        this.estacionFin=estacionFin;
        this.tiempo=tiempo;
    }
    //getter and setter
    public int getTiempo(){
        return tiempo;
    }

    public Estacion getEstacionIni(){
        return estacionIni;
    }

    public Estacion getEstacionFin(){
        return estacionFin;
    }

    //methods
}
