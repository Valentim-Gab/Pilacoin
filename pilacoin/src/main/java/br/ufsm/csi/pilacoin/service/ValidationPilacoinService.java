package br.ufsm.csi.pilacoin.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.json.DifficultJson;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.json.ValidationPilaCoinJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;

// @Service
public class ValidationPilacoinService {
    @Value("${queue.pilacoin.mined}")
    private String pilaMineradoQueue;

    @Value("${queue.pilacoin.valided}")
    private String pilaValidedQueue;

    private DifficultService difficultService;
    private RabbitTemplate rabbitTemplate;
    private CryptoUtil cryptoUtil;
    private PilacoinService pilacoinService;

    public ValidationPilacoinService(RabbitTemplate rabbitTemplate, DifficultService difficultService,
            CryptoUtil cryptoUtil, PilacoinService pilacoinService) {
        this.rabbitTemplate = rabbitTemplate;
        this.difficultService = difficultService;
        this.cryptoUtil = cryptoUtil;
        this.pilacoinService = pilacoinService;
    }

    @RabbitListener(queues = { "${queue.pilacoin.mined}" })
    public void verifyMinedPila(@Payload String strJson) {
        try {
            ObjectMapper om = new ObjectMapper();
            PilaCoinJson pilaCoin = null;
            pilaCoin = om.readValue(strJson, PilaCoinJson.class);

            if (pilaCoin.getNomeCriador().contains("Gabriel_Valentim")) {
                rabbitTemplate.convertAndSend(pilaMineradoQueue, strJson);

                return;
            }

            while (difficultService.difficultJson == null) {
                Thread.sleep(1000);
            }

            System.out.println("\n\n[VERIFYING PILACOIN]: " + pilaCoin.getNomeCriador());

            DifficultJson difficultJson = difficultService.difficultJson;
            BigInteger difficult = new BigInteger(difficultJson.getDificuldade(), 16).abs();
            BigInteger hash = cryptoUtil.generatehash(strJson);

            if (hash.compareTo(difficult) < 0) {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, cryptoUtil.generateKeys().getPrivate());

                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] signature = cipher.doFinal(md.digest(strJson.getBytes(StandardCharsets.UTF_8)));

                Base64.getEncoder().encodeToString(signature);

                ValidationPilaCoinJson vPilaCoin = ValidationPilaCoinJson.builder()
                        .pilaCoinJson(pilaCoin)
                        .assinaturaPilaCoin(signature)
                        .chavePublicaValidador(cryptoUtil.generateKeys().getPublic().getEncoded())
                        .nomeValidador("Gabriel_Valentim").build();

                System.out.println("[VALID PILACOIN]: " + pilaCoin.getNonce());

                rabbitTemplate.convertAndSend(pilaValidedQueue,
                        om.writeValueAsString(vPilaCoin));
            } else {
                System.out.println("[INVALID PILACOIN]: " + pilaCoin.getNonce());
            }
        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | NoSuchPaddingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // @RabbitListener(queues = { "${queue.pilacoin.valided.user}" })
    // public void receiveValidedPila(@Payload String strJson) {
    // try {
    // ObjectMapper om = new ObjectMapper();
    // ValidationPilaCoinJson vPilaCoin = null;
    // vPilaCoin = om.readValue(strJson, ValidationPilaCoinJson.class);
    // String nomeCriador = vPilaCoin.getPilaCoinJson().getNomeCriador();

    // if (!nomeCriador.equals("Gabriel_Valentim")) {
    // System.out.println("\n\n[RECEIVED]: " + strJson);
    // rabbitTemplate.convertAndSend(pilaValidedUserQueue, strJson);

    // return;
    // }

    // System.out.println("\n\n[ATUALIZANDO]: " + strJson);

    // pilacoinService.update(vPilaCoin.getPilaCoinJson(),
    // PilaCoin.StatusPila.VALIDO);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}
