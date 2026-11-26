public class AppleWallet {
    public String enlaceAppleWallet(Reserva reserva) {
        return "/reserva/" + reserva.getCod() + "/apple-wallet-demo";
    }
}
/*
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ApplePassService {

    private final String passTypeId;     // ej: pass.com.tuempresa.trenes (lo creas en developer.apple.com)
    private final String teamId;         // Team ID de tu cuenta Apple Developer
    private final PrivateKey clavePrivada;
    private final X509Certificate certificado; // tu certificado Pass Type ID
    private final X509Certificate wwdr;        // certificado intermedio de Apple (WWDR)

    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    public ApplePassService(String passTypeId, String teamId,
                            String p12Path, String p12Password, String wwdrPath) throws Exception {
        this.passTypeId = passTypeId;
        this.teamId = teamId;

        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream in = new FileInputStream(p12Path)) {
            ks.load(in, p12Password.toCharArray());
        }
        String alias = ks.aliases().nextElement();
        this.clavePrivada = (PrivateKey) ks.getKey(alias, p12Password.toCharArray());
        this.certificado = (X509Certificate) ks.getCertificate(alias);

        try (InputStream in = new FileInputStream(wwdrPath)) {
            this.wwdr = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
        }
    }


    public static ApplePassService desdeEntorno() throws Exception {
        return new ApplePassService(
                System.getenv("APPLE_PASS_TYPE_ID"),
                System.getenv("APPLE_TEAM_ID"),
                System.getenv("APPLE_P12_PATH"),
                System.getenv("APPLE_P12_PASSWORD"),
                System.getenv("APPLE_WWDR_PATH"));
    }

    public byte[] generarPkpass(Reserva reserva) throws Exception {
        // 1) Ficheros que van dentro del pase
        Map<String, byte[]> ficheros = new LinkedHashMap<>();
        ficheros.put("pass.json", crearPassJson(reserva).getBytes(StandardCharsets.UTF_8));
        ficheros.put("icon.png", leerRecurso("/wallet/icon.png"));   // obligatorio
        ficheros.put("logo.png", leerRecurso("/wallet/logo.png"));   // opcional pero recomendable

        // 2) manifest.json: hash SHA-1 de cada fichero
        Map<String, String> hashes = new LinkedHashMap<>();
        for (Map.Entry<String, byte[]> f : ficheros.entrySet()) {
            hashes.put(f.getKey(), sha1Hex(f.getValue()));
        }
        byte[] manifest = new ObjectMapper().writeValueAsBytes(hashes);

        // 3) signature: firma PKCS#7 (separada) del manifest
        byte[] firma = firmar(manifest);

        // 4) Empaquetar todo en un ZIP
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(salida)) {
            for (Map.Entry<String, byte[]> f : ficheros.entrySet()) {
                anadir(zip, f.getKey(), f.getValue());
            }
            anadir(zip, "manifest.json", manifest);
            anadir(zip, "signature", firma);
        }
        return salida.toByteArray();
    }

    private String crearPassJson(Reserva reserva) throws Exception {
        String origen = reserva.getTren().getRecorrido().getEstacionIni().getNombre();
        String destino = reserva.getTren().getRecorrido().getEstacionFin().getNombre();

        Map<String, Object> pass = new LinkedHashMap<>();
        pass.put("formatVersion", 1);
        pass.put("passTypeIdentifier", passTypeId);
        pass.put("teamIdentifier", teamId);
        pass.put("serialNumber", reserva.getCod());
        pass.put("organizationName", "MiApp Trenes");
        pass.put("description", "Billete de tren");
        pass.put("logoText", "MiApp Trenes");
        pass.put("backgroundColor", "rgb(0, 102, 204)");
        pass.put("foregroundColor", "rgb(255, 255, 255)");
        pass.put("labelColor", "rgb(220, 230, 245)");

        // Código QR con el código de reserva
        pass.put("barcodes", List.of(Map.of(
                "format", "PKBarcodeFormatQR",
                "message", reserva.getCod(),
                "messageEncoding", "iso-8859-1")));

        // Diseño de billete de transporte
        Map<String, Object> boarding = new LinkedHashMap<>();
        boarding.put("transitType", "PKTransitTypeTrain");
        boarding.put("primaryFields", List.of(
                campo("origen", "ORIGEN", origen),
                campo("destino", "DESTINO", destino)));
        boarding.put("secondaryFields", List.of(
                campo("fecha", "FECHA", reserva.getTren().getFecha().toString()),
                campo("hora", "HORA", reserva.getHoraSalida().toString())));
        boarding.put("auxiliaryFields", List.of(
                campo("precio", "PRECIO", reserva.getPrecio() + " €"),
                campo("codigo", "RESERVA", reserva.getCod())));
        pass.put("boardingPass", boarding);

        return new ObjectMapper().writeValueAsString(pass);
    }

    private Map<String, Object> campo(String key, String label, String value) {
        return Map.of("key", key, "label", label, "value", value);
    }

    private byte[] firmar(byte[] datos) throws Exception {
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC").build(clavePrivada);

        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                .build(signer, certificado));

        // Se incluyen tu certificado y el WWDR de Apple
        gen.addCertificates(new JcaCertStore(List.of(certificado, wwdr)));

        CMSSignedData firmado = gen.generate(new CMSProcessableByteArray(datos), false);
        return firmado.getEncoded();
    }

    private String sha1Hex(byte[] datos) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-1").digest(datos);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private void anadir(ZipOutputStream zip, String nombre, byte[] contenido) throws Exception {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido);
        zip.closeEntry();
    }

    private byte[] leerRecurso(String ruta) throws Exception {
        try (InputStream in = getClass().getResourceAsStream(ruta)) {
            if (in == null) throw new IllegalStateException("Falta el recurso " + ruta);
            return in.readAllBytes();
        }
    }
}
*/