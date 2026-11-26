public class GoogleWallet {

    public String enlaceGoogleWallet(Reserva reserva) {
        return "/reserva/" + reserva.getCod() + "/wallet-demo";
    }


}
/*QUEDA COMENTADO EL METODO REAL

 import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

 public class WalletService {

     private final String issuerId;            // ID de emisor (Google Pay & Wallet Console)
     private final String serviceAccountEmail; // "client_email" del JSON de la service account
     private final RSAPrivateKey clavePrivada; // "private_key" del JSON de la service account

     public WalletService(String issuerId, String serviceAccountEmail, String privateKeyPem) throws Exception {
         this.issuerId = issuerId;
         this.serviceAccountEmail = serviceAccountEmail;
         this.clavePrivada = cargarClave(privateKeyPem);
     }


public static WalletService desdeEntorno() throws Exception {
    return new WalletService(
            System.getenv("WALLET_ISSUER_ID"),
            System.getenv("WALLET_SA_EMAIL"),
            System.getenv("WALLET_PRIVATE_KEY"));
}


private RSAPrivateKey cargarClave(String pem) throws Exception {
    String limpio = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")      // por si los saltos de línea llegan como texto "\n"
            .replaceAll("\\s", "");
    byte[] der = Base64.getDecoder().decode(limpio);
    return (RSAPrivateKey) KeyFactory.getInstance("RSA")
            .generatePrivate(new PKCS8EncodedKeySpec(der));
}


public String enlaceGoogleWallet(Reserva reserva) {
    String origen = reserva.getTren().getRecorrido().getEstacionIni().getNombre();
    String destino = reserva.getTren().getRecorrido().getEstacionFin().getNombre();

    // Datos del pase (un "objeto" basado en la clase billete_tren creada en la consola)
    Map<String, Object> objeto = new LinkedHashMap<>();
    objeto.put("id", issuerId + "." + reserva.getCod());       // único por reserva
    objeto.put("classId", issuerId + ".billete_tren");          // debe existir en la consola
    objeto.put("state", "ACTIVE");
    objeto.put("cardTitle", texto("Billete de tren"));
    objeto.put("header", texto(origen + " - " + destino));
    objeto.put("subheader", texto(reserva.getTren().getFecha() + " " + reserva.getHoraSalida()));
    objeto.put("barcode", Map.of("type", "QR_CODE", "value", reserva.getCod()));

    Map<String, Object> payload = Map.of("genericObjects", List.of(objeto));


    String jwt = JWT.create()
            .withIssuer(serviceAccountEmail)
            .withAudience("google")
            .withClaim("typ", "savetowallet")
            .withIssuedAt(Instant.now())
            .withClaim("payload", payload)
            .sign(Algorithm.RSA256(null, clavePrivada));

    return "https://pay.google.com/gp/v/save/" + jwt;
}


private Map<String, Object> texto(String valor) {
    return Map.of("defaultValue", Map.of("language", "es", "value", valor));
}
    }
*/