package br.ufsm.csi.pilacoin.service;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

import br.ufsm.csi.pilacoin.model.json.TypeActionWsJson;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.constant.UserConstant;
import br.ufsm.csi.pilacoin.model.json.BlockJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;
import br.ufsm.csi.pilacoin.utils.StrUtil;
import br.ufsm.csi.pilacoin.web.WebSocketService;

@Service
public class MiningBlockService {
    private final DifficultService difficultService;
    private final RabbitTemplate rabbitTemplate;
    private final WebSocketService webSocketService;

    @Value("${queue.bloco.mined}")
    private String blockMinedQueue;

    public MiningBlockService(DifficultService difficultService, RabbitTemplate rabbitTemplate,
            WebSocketService webSocketService) {
        this.difficultService = difficultService;
        this.rabbitTemplate = rabbitTemplate;
        this.webSocketService = webSocketService;
    }

    @RabbitListener(queues = { "${queue.block.find}" })
    public void mine(@Payload String strJson) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (strJson == null) {
                            return;
                        }

                        BigInteger dificuldade = difficultService.getDifficult();
                        ObjectMapper mapper = new ObjectMapper();
                        BlockJson block = mapper.readValue(strJson, BlockJson.class);

                        System.out.println("\n\n[MINING BLOCK] - número: " + block.getNumeroBloco());
                        webSocketService.send("MINING BLOCK - número: " + block.getNumeroBloco(),
                                "/topic/pilacoin", TypeActionWsJson.TypeAction.MINER_BLOCK);

                        BlockJson minedBlock = BlockJson.builder()
                                .numeroBloco(block.getNumeroBloco())
                                .nonceBlocoAnterior(block.getNonce())
                                .chaveUsuarioMinerador(CryptoUtil.generateKeys().getPublic().getEncoded())
                                .nomeUsuarioMinerador(UserConstant.USERNAME)
                                .build();

                        byte[] bNum = new byte[256 / 8];
                        Random random = new Random(System.currentTimeMillis());

                        do {
                            random.nextBytes(bNum);
                            minedBlock.setNonce(new BigInteger(bNum).abs().toString());
                        } while (CryptoUtil.generatehash(minedBlock).compareTo(dificuldade) > 0);

                        if (difficultService.getFinalValidity().compareTo(new Date()) > 0 || true) {
                            Thread.sleep(500);

                            String blockStr = mapper.writeValueAsString(minedBlock);

                            System.out.println("\n\n[BLOCO MINERADO]: " + blockStr);
                            webSocketService.send("BLOCO MINERADO - nonce: " + StrUtil.
                                            limitCharsAddEllipsis(minedBlock.getNonce(), 10),
                                    "/topic/pilacoin", TypeActionWsJson.TypeAction.MINER_BLOCK);

                            rabbitTemplate.convertAndSend(blockMinedQueue, blockStr);
                            Thread.sleep(10000);
                        }
                    }
                } catch (InterruptedException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
