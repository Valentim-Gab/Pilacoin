package br.ufsm.csi.pilacoin.service;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.json.BlockJson;
import br.ufsm.csi.pilacoin.model.json.DifficultJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;

@Service
public class MinerBlockService {
  private DifficultService difficultService;
    private CryptoUtil cryptoUtil;
    public RabbitTemplate rabbitTemplate;

    @Value("${queue.bloco.mined}")
    private String blockMinedQueue;

    public MinerBlockService(DifficultService difficultService, CryptoUtil cryptoUtil, RabbitTemplate rabbitTemplate) {
        this.difficultService = difficultService;
        this.cryptoUtil = cryptoUtil;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {"${queue.block.find}"})
    public void mine(@Payload BlockJson block) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (difficultService.difficultJson == null) {
                            Thread.sleep(1000);

                            continue;
                        }

                        DifficultJson difficultJson = difficultService.difficultJson;
                        BigInteger dificuldade = new BigInteger(difficultJson.getDificuldade(), 16).abs();

                        System.out.println("Chegou");
                        System.out.println(dificuldade);
                        System.out.println(block.toString());

                        BlockJson minedBlock = BlockJson.builder() 
                                .numeroBloco(block.getNumeroBloco())
                                .nonceBlocoAnterior(block.getNonce())
                                .chaveUsuarioMinerador(cryptoUtil.pair.getPublic().getEncoded())
                                .nomeUsuarioMinerador("Gabriel Valentim")
                                .build();

                        byte[] bNum = new byte[256/8];
                        Random random = new Random(System.currentTimeMillis());

                        do {
                            random.nextBytes(bNum);
                            minedBlock.setNonce(new BigInteger(bNum).abs().toString());
                        } while (cryptoUtil.generatehash(minedBlock).compareTo(dificuldade) > 0);

                        if (difficultJson.getValidadeFinal().compareTo(new Date()) > 0 || true) {
                            ObjectMapper mapper = new ObjectMapper();
                            String blockStr = mapper.writeValueAsString(minedBlock);

                            // rabbitTemplate.convertAndSend(blockMinedQueue, blockStr);
                        }
                    }
                } catch (InterruptedException | JsonProcessingException | NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
