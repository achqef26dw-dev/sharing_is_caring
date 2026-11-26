public class CodigoReserva {
    private String codigo;

    public CodigoReserva() {
        this.codigo = codigoAleatorio();
    }

    private String codigoAleatorio(){
        this.codigo = "";
        String[] letras = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

        for (int i = 0; i < 8; i++){
            int num =(int) Math.random() * letras.length;

            this.codigo += letras[num];
        }
        int num =(int) Math.random() * 10;


        this.codigo += num;

        return this.codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
