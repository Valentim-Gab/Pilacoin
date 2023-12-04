package br.ufsm.csi.pilacoin.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class CryptoUtil {
    private String keyFilePath = "src/main/resources/static/keys";

    public BigInteger generatehash(Object object) {
        try {
            String json = null;

            if (object instanceof String) {
                json = (String) object;
            } else {
                ObjectMapper om = new ObjectMapper();
                json = om.writeValueAsString(object);
            }

            MessageDigest md = MessageDigest.getInstance("SHA-256");

            return new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8))).abs();
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            e.printStackTrace();

            return null;
        }
    }

    public KeyPair generateKeys() {
        try {
            File file = new File(keyFilePath + "/keypair.der");
            KeyPair pair = null;

            if (file.exists()) {
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                    pair = (KeyPair) in.readObject();
                }
            } else {
                if (!Files.exists(Paths.get(keyFilePath)))
                    Files.createDirectories(Paths.get(keyFilePath));

                try (ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream(keyFilePath + "/keypair.der"))) {
                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                    generator.initialize(2048);

                    pair = generator.generateKeyPair();
                    out.writeObject(pair);
                }
            }

            return pair;
        } catch (NoSuchAlgorithmException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
