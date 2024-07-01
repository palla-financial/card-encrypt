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
        // Get the key file from Palla bucket
        // Sandbox: https://palla-public-keys.s3.us-east-2.amazonaws.com/card-encrypt/sandbox.json
        // Production: https://palla-public-keys.s3.us-east-2.amazonaws.com/card-encrypt/prod.json
        String pubKey = "<pem encoded key file contents>";
        
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