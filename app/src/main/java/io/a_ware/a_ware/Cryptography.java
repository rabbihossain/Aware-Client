package io.a_ware.a_ware;

/**
 * Created by admin on 2017-09-21.
 */
import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;
import android.util.Base64;
import android.util.Log;;
import javax.crypto.spec.IvParameterSpec;

public class Cryptography {
    public static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final int SymmetricKeyLength = 16; //bytes= 256 bits
    public static final int offset = 0;
    public static final String ALGORITHMKEYSPEC = "AES";
    //Phex och Ghex hamtade fran https://tools.ietf.org/html/rfc5114 section 2.3
    public static String Phex = "87A8E61DB4B6663CFFBBD19C651959998CEEF608660DD0F25D2CEED4435E3B00E00DF8F1D61957D4FAF7DF4561B2AA3016C3D91134096FAA3BF4296D830E9A7C209E0C6497517ABD5A8A9D306BCF67ED91F9E6725B4758C022E0B1EF4275BF7B6C5BFC11D45F9088B941F54EB1E59BB8BC39A0BF12307F5C4FDB70C581B23F76B63ACAE1CAA6B7902D52526735488A0EF13C6D9A51BFA4AB3AD8347796524D8EF6A167B5A41825D967E144E5140564251CCACB83E6B486F6B3CA3F7971506026C0B857F689962856DED4010ABD0BE621C3A3960A54E710C375F26375D7014103A4B54330C198AF126116D2276E11715F693877FAD7EF09CADB094AE91E1A1597";
    public static String Ghex = "3FB32C9B73134D0B2E77506660EDBD484CA7B18F21EF205407F4793A1A0BA12510DBC15077BE463FFF4FED4AAC0BB555BE3A6C1B0C6B47B1BC3773BF7E8C6F62901228F8C28CBB18A55AE31341000A650196F931C77A57F2DDF463E5E9EC144B777DE62AAAB8A8628AC376D282D6ED3864E67982428EBC831D14348F6F2F9193B5045AF2767164E1DFC967C1FB3F2E55A4BD1BFFE83B9C80D052B985D182EA0ADB2A3B7313D3FE14C8484B1E052588B9B7D2BBD2DF016199ECD06E1557CD0915B3353BBB64E0EC377FD028370DF92B52C7891428CDC67EB6184B523D1DB246C32F63078490F00EF8D647D148D47954515E2327CFEF98C582664B4C0F6CC41659";
    public static int randomNumberBitSize = 2048;

    public SecureRandom generateSecureRandom(){//creates a secureRandom instance and returns it
        SecureRandom random = new SecureRandom();
        return random;
    }

    //Encrypts the string passed as message with sharedKey as secretKey and initVector as IV
    public String encryption(String message, BigInteger sharedKey, byte[] initVector){
        String encMsg="";
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            Key key = new SecretKeySpec(sharedKey.toString().getBytes("UTF8"), offset, SymmetricKeyLength, ALGORITHMKEYSPEC);//Creates the key from the sharedKey to an AES key
            IvParameterSpec iv = new IvParameterSpec(initVector);			//Creates an IV from the initVector
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] ciphertext = cipher.doFinal(message.getBytes("UTF8"));	// Encrypt the message
            encMsg = Base64.encodeToString(ciphertext,Base64.NO_WRAP);		// Encode the ciphertext
        }
        catch(Exception e){
            Log.e("Cryptography.java: ","Error in encryption "+ e);
        }
        return encMsg;
    }

    //Decrypts the passed ciphertext with the sharedKey as secretKey and initVector as IV
    public String decryption(String ciphertext, BigInteger sharedKey, byte[] initVector){
        String message="";
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            Key key = new SecretKeySpec(sharedKey.toString().getBytes("UTF8"), offset, SymmetricKeyLength, ALGORITHMKEYSPEC);//Creates the key from the sharedKey to an AES key
            IvParameterSpec iv = new IvParameterSpec(initVector);			//Creates an IV from the initVector
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] messageBytes = cipher.doFinal(Base64.decode(ciphertext, Base64.NO_WRAP)); 	//Decode the message and then decrypt it
            message = new String(messageBytes);												//Convert the message bytes to a string
        }
        catch(Exception e){
            System.out.println("Error in decryption " + e);
        }
        return message;
    }

    public byte[] generateIv(){
        byte [] rndBytes= new byte[16];					// 16 = 128 bits which is the block size in cbc
        generateSecureRandom().nextBytes(rndBytes);		// Uses a secureRandom instance to get random bytes into rndBytes
        return rndBytes;
    }
}
