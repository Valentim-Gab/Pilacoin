package br.ufsm.csi.pilacoin.utils;

import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
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

    public BigInteger generatehash(PilaCoinJson pilaCoinJson) throws NoSuchAlgorithmException, JsonProcessingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String json = om.writeValueAsString(pilaCoinJson);
        BigInteger hash = new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8)));

        return hash.abs();
    }
}
