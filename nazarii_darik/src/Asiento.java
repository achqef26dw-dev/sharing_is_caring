public class Asiento {
    private String modelo;
    private double precio;
    private boolean disponible;

    //constructor
    public Asiento(String modelo, double precio){
        this.modelo = modelo;
        this.precio=precio;
        disponible = true;
    }

    //getter and setter
    public double getPrecio(){
        return precio;
    }
    public String getModelo(){
        return modelo;
    }
    public boolean getDispo(){
        return disponible;
    }

    //methods
    public void ocuparAsiento(){
        disponible = false;
    }
}
