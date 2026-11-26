import java.util.Scanner;

public class Compra_Billetes {

    private Trenes tren;
    private boolean[][] asientos = new boolean[100][4]; ///asientos total del tren
    private String codigo;



    public Compra_Billetes(Trenes tren) {
        CodigoReserva codigo = new CodigoReserva();

        if (tren == null){
            // tren.algo
            /// se necesita la clase de busqueda
        } // se asegura de que el usuario si realice la busqueda
        this.tren = tren;

        this.codigo = codigo.getCodigo();
    }

    private void asientos(){
        Scanner sc = new Scanner(System.in);
        int aux1 = 0, aux2 = 0;

        System.out.println("Cantidad de asientos que desea: ");
        while (aux1 < 1){
            int cantAsientos = sc.nextInt();

            if (cantAsientos > 0 && cantAsientos <= asientos.length){

                for (int i = 0; i < asientos.length; i++){
                    for (int j = 0; j < asientos[i].length; j++){
                        if (asientos[i][j] == false){
                            aux2++;
                        }
                    }
                }

                if (cantAsientos > aux2 ){
                    System.out.println("No hay tantos asientos libres.");
                } else {
                    System.out.println("Se le asignara de forma automatica los primeros asientos disponibles.");
                    for (int i = 0; i < asientos.length; i++){
                        //controlador para asignar las sillas
                        controladorAsientos(cantAsientos);

                    }
                }
                aux1++;
            }
        }

    }


    private void controladorAsientos(int cantidad){
        int aux = 0;

        /// si solo se solicita un asiento, se le asignara el primer asiento libre que se encuentre
        if (cantidad == 1){
            for (int i = 0; i < asientos.length; i++){
                for (int j = 0; j < asientos[i].length; j++){

                    if (asientos[i][j] == true) {
                        asientos[i][j] = false;
                        /// de momento se adignara en el array generico del tren, si el asiento estara ocupado
                        /// o si no lo esta meediante el true y false
                        System.out.println("Asiento asignado: " + asientos[i]);
                    }
                }
            }

        } else if (cantidad%2 == 0){
            for (int i = 0; i < asientos.length; i++){
                for (int j = 0; j < asientos[i].length; j++) {

                    if (asientos[i][j] == true && (i % 2 == 0 || aux > 0)) {
                        asientos[i][j] = false;
                        /// de momento se adignara en el array generico del tren, si el asiento estara ocupado
                        /// o si no lo esta meediante el true y false
                        System.out.println("Asiento asignado: " + asientos[i][j]);
                        aux++;
                    }
                    if (asientos[i][j] == false) {
                        // en el caso de que en la siguiente linea la ocupe alguien se reinicia el auxiliar
                        // ya que si hay mas pesronas se asignara una linea para las siguientes dos personas
                        aux = 0;
                    }
                }
            }
        } else {
            for (int i = 0; i < asientos.length; i++){
                for (int j = 0; j < asientos[i].length; j++) {

                    if (asientos[i][j] == true) {
                        asientos[i][j]= false;
                        /// de momento se adignara en el array generico del tren, si el asiento estara ocupado
                        /// o si no lo esta meediante el true y false
                        System.out.println("Asiento asignado: " + asientos[i]);
                        aux++;
                    }
                    if (asientos[i][j] == false) {
                        // en el caso de que en la siguiente linea la ocupe alguien se reinicia el auxiliar
                        // ya que si hay mas pesronas se asignara una linea para las siguientes dos personas
                        aux = 0;
                    }
                }
            }
        }
    }


}
