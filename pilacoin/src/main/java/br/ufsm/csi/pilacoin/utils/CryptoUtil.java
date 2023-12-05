package br.ufsm.csi.pilacoin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

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

import javax.crypto.Cipher;

@Component
public class CryptoUtil {
    private static String keyFilePath = "src/main/resources/static/keys";

    @SneakyThrows
    private static byte[] digest(String jsonStr) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        return md.digest(jsonStr.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static BigInteger generatehash(Object object) {
        String json = null;

        if (object instanceof String) {
            json = (String) object;
        } else {
            ObjectMapper om = new ObjectMapper();
            json = om.writeValueAsString(object);
        }

        return new BigInteger(digest(json)).abs();
    }

    @SneakyThrows
    public static byte[] generateSignature(Object json) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(json);
        Cipher cipher = Cipher.getInstance("RSA");

        cipher.init(Cipher.ENCRYPT_MODE, generateKeys().getPrivate());

        return cipher.doFinal(digest(jsonStr));
    }

    public static KeyPair generateKeys() {
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
