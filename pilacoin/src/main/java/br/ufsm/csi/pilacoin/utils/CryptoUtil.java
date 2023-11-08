package br.ufsm.csi.pilacoin.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class CryptoUtil {
    public KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    public KeyPair pair = generator.generateKeyPair();
    private ObjectMapper om = new ObjectMapper();

    public CryptoUtil() throws NoSuchAlgorithmException {
    }

    public BigInteger generatehash(Object object) throws NoSuchAlgorithmException, JsonProcessingException {
        String json = null;

        if (object instanceof String) {
            json = (String) object;
        } else {
            this.om = new ObjectMapper();
            this.om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            json = this.om.writeValueAsString(object);
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash = new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8)));

        return hash.abs();
    }
}
