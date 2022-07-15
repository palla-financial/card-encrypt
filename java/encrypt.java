import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

class Main {  
    public static void main(String args[]) { 
        // Get string of PEM encoded RSA public key data
        String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsT+2UBDBHXFVng0tsuUtgU5syromdZGNcpoK60pBLleXjAavLKkgA58YiPUFCuxjRIQsp5Azcxckv76xuZvI+hJc6UqilxydBYigM5jofJMQv4OcBOmylqsrE+8A7T6RGQ9CP63TqA2bBdZsocm3yCPne2CHRfIOvlvEeBSD5hNyCpEzRx57iitMRqC7N6prGiJd9wEmoPp9X0eux4i+0FawAZ2DJTl3rk1dJ0HOmLOX6E3Pu75WwREzWqiT/aYY3B2L/Qd0Od5vNDUG3qaLP22PQGGEfbGI7a9hPIBqxshU5G4hMHSvGNJL24yS+78tefFjZFKjU6k1KlpUCe4AxQIDAQAB";        ;
        
        // Format card data into correct format, pipe-delineated string
        // Full card number
        // Expiration in YYYYMM format
        // CVV Code
        String cardData = String.join("|", "4000056655665556", "202412", "111");
        
        try {
            // Decode and instantiate RSA public key
            RSAPublicKey publicKey = getPublicKey(pubKey);
            
            byte[] plaintext = cardData.getBytes(StandardCharsets.UTF_8);
            
            // Encrypt card data bytes with public key
            byte[] ciphertext = encrypt(publicKey, plaintext);
            
            // Encode ciphertext with URL Safe Base64
            String encoded = Base64.getUrlEncoder().encodeToString(ciphertext);
            
            // Output
            System.out.println(encoded);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
    
    private static RSAPublicKey getPublicKey(String pubKey) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey));
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
    
    private static byte[] encrypt(PublicKey key, byte[] plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding"); 
        
        // Important to set this as SHA-256 is not the default provider's hash function
        OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        
        cipher.init(Cipher.ENCRYPT_MODE, key, oaepParameterSpec);
        return cipher.doFinal(plainText);
    }
}