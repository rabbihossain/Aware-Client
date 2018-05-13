package io.a_ware.a_ware;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

   /* @Test
    public void encryption_isCorrect() throws Exception {
        Cryptography crypto = new Cryptography();
        String msg = "Test123";
        BigInteger key = new BigInteger("872954872336337980201756407830377619942678236917");
        byte[] Iv = crypto.generateIv();
        String encryptedMsg1 = crypto.encryption(msg, key, crypto.generateIv());
        String encryptedMsg2 = crypto.encryption(msg, key, crypto.generateIv());
        assertNotEquals(encryptedMsg1, encryptedMsg2);
    }*/

}