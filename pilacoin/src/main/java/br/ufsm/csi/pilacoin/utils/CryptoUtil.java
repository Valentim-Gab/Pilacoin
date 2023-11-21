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
    private ObjectMapper om = new ObjectMapper();

    public BigInteger generatehash(Object object) {
        try {
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
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void generateKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            KeyPair pair = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
