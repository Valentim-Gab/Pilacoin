package br.ufsm.csi.pilacoin.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.ufsm.csi.pilacoin.model.json.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;
import jakarta.annotation.PostConstruct;

@Service
public class ValidationPilacoinService {
    @Value("${queue.pilacoin.mined}")
    private String pilaMineradoQueue;

    @Value("${queue.pilacoin.valided}")
    private String pilaValidedQueue;

    @Value("${queue.query}")
    private String query;

    private DifficultService difficultService;
    private RabbitTemplate rabbitTemplate;
    private CryptoUtil cryptoUtil;
    private PilacoinService pilacoinService;

    private final SimpMessagingTemplate template;

    public ValidationPilacoinService(RabbitTemplate rabbitTemplate, DifficultService difficultService,
            CryptoUtil cryptoUtil, PilacoinService pilacoinService, SimpMessagingTemplate template) {
        this.rabbitTemplate = rabbitTemplate;
        this.difficultService = difficultService;
        this.cryptoUtil = cryptoUtil;
        this.pilacoinService = pilacoinService;
        this.template = template;
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

            TypeActionWsJson typeActionWsJson = TypeActionWsJson.builder()
                    .message("VERIFYING PILACOIN - criador: " + pilaCoin.getNomeCriador())
                    .type(TypeActionWsJson.TypeAction.VALIDATION_PILACOIN)
                    .timestamp(System.currentTimeMillis())
                    .build();

            template.convertAndSend("/topic/pilacoin",
                    om.writeValueAsString(typeActionWsJson));

            DifficultJson difficultJson = difficultService.difficultJson;
            BigInteger difficult = new BigInteger(difficultJson.getDificuldade(), 16).abs();
            BigInteger hash = cryptoUtil.generatehash(strJson);

            if (hash.compareTo(difficult) < 0) {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, cryptoUtil.generateKeys().getPrivate());

                String newPilaStr = om.writeValueAsString(pilaCoin);
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] signature = cipher.doFinal(md.digest(newPilaStr.getBytes(StandardCharsets.UTF_8)));

                ValidationPilaCoinJson vPilaCoin = ValidationPilaCoinJson.builder()
                        .pilaCoinJson(pilaCoin)
                        .assinaturaPilaCoin(signature)
                        .chavePublicaValidador(cryptoUtil.generateKeys().getPublic().getEncoded())
                        .nomeValidador("Gabriel_Valentim").build();

                System.out.println("[VALID PILACOIN]: " + pilaCoin.getNonce());

                String formattedNonce =  pilaCoin.getNonce().
                        substring(0, Math.min(pilaCoin.getNonce().length(), 10)) + "...";

                typeActionWsJson.setMessage("VALID PILA - nonce: " + formattedNonce);
                typeActionWsJson.setTimestamp(System.currentTimeMillis());
                template.convertAndSend("/topic/pilacoin",
                        om.writeValueAsString(typeActionWsJson));

                rabbitTemplate.convertAndSend(pilaValidedQueue,
                        om.writeValueAsString(vPilaCoin));
            } else {
                System.out.println("[INVALID PILACOIN]: " + pilaCoin.getNonce());

                typeActionWsJson.setMessage("INVALID PILA - criador: " + pilaCoin.getNomeCriador());
                typeActionWsJson.setTimestamp(System.currentTimeMillis());
                template.convertAndSend("/topic/pilacoin",
                        om.writeValueAsString(typeActionWsJson));
            }
        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | NoSuchPaddingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void receiveValidedPila() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        QueryJson queryJson = QueryJson.builder()
                                .idQuery(2l)
                                .nomeUsuario("Gabriel_Valentim")
                                .tipoQuery(QueryJson.TypeQuery.PILA)
                                .build();

                        ObjectMapper om = new ObjectMapper();

                        rabbitTemplate.convertAndSend(query, om.writeValueAsString(queryJson));

                        Thread.sleep(10000);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
