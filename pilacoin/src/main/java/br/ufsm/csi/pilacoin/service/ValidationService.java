// package br.ufsm.csi.pilacoin.service;

// import java.math.BigInteger;
// import java.nio.charset.StandardCharsets;
// import java.security.InvalidKeyException;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Base64;

// import javax.crypto.BadPaddingException;
// import javax.crypto.Cipher;
// import javax.crypto.IllegalBlockSizeException;
// import javax.crypto.NoSuchPaddingException;

// import org.springframework.amqp.rabbit.annotation.RabbitListener;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.messaging.handler.annotation.Payload;
// import org.springframework.stereotype.Service;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import br.ufsm.csi.pilacoin.model.json.DifficultJson;
// import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
// import br.ufsm.csi.pilacoin.model.json.ValidationPilaCoinJson;
// import br.ufsm.csi.pilacoin.utils.CryptoUtil;

// @Service
// public class ValidationService {
//     @Value("${queue.pilacoin.minerado}")
//     private String pilaMineradoQueue;

//     @Value("${queue.pilacoin.validado}")
//     private String pilaValidedQueue;

//     private DifficultService difficultService;
//     private RabbitTemplate rabbitTemplate;
//     private CryptoUtil cryptoUtil;

//     public ValidationService(RabbitTemplate rabbitTemplate, DifficultService difficultService, CryptoUtil cryptoUtil) {
//         this.rabbitTemplate = rabbitTemplate;
//         this.difficultService = difficultService;
//         this.cryptoUtil = cryptoUtil;
//     }

//     @RabbitListener(queues = {"${queue.pilacoin.minerado}"})
//     public void verifyMinedPila(@Payload String strJson) {
//         try {
//             ObjectMapper om = new ObjectMapper();
//             PilaCoinJson pilaCoin = null;
//             pilaCoin = om.readValue(strJson, PilaCoinJson.class);

            
//             if (pilaCoin.getNomeCriador().contains("Gabriel Valentim")) {
//                 rabbitTemplate.convertAndSend(pilaMineradoQueue, strJson);

//                 return;
//             }

//             System.out.println("[VERIFICANDO]: " + pilaCoin.toString());

//             while (difficultService.difficultJson == null) {
//                 //System.out.println("[WAITING]: Dificuldade");
//             }

//             DifficultJson difficultJson = difficultService.difficultJson;
            
//             BigInteger difficult = new BigInteger(difficultJson.getDificuldade(), 16).abs();
//             BigInteger hash = cryptoUtil.generatehash(pilaCoin).abs();

//             if (hash.compareTo(difficult) < 0) {
//                 Cipher cipher = Cipher.getInstance("RSA");
//                 cipher.init(Cipher.ENCRYPT_MODE, cryptoUtil.pair.getPrivate());

//                 MessageDigest md = MessageDigest.getInstance("SHA-256");
//                 byte[] signature = cipher.doFinal(md.digest(strJson.getBytes(StandardCharsets.UTF_8)));

//                 System.out.println("[SIGNATURE]: " + Base64.getEncoder().encodeToString(signature));

//                 ValidationPilaCoinJson vPilaCoin = ValidationPilaCoinJson.builder()
//                         .pilaCoin(pilaCoin)
//                         .assinaturaPilaCoin(signature)
//                         .chavePublicaValidador(cryptoUtil.pair.getPublic().getEncoded())
//                         .nomeValidador("Gabriel Valentim").build();

//                 rabbitTemplate.convertAndSend(pilaValidedQueue, om.writeValueAsString(vPilaCoin));
//             } else {
//                 System.out.println("[INVALID]: " + pilaCoin.getNonce());
//             }
//         } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeyException  | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
//             e.printStackTrace();
//         }
//     }
// }
