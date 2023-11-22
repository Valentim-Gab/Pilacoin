package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.json.DifficultJson;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

//@Service
public class MineracaoService {
    private DifficultService difficultService;
    private CryptoUtil cryptoUtil;
    public RabbitTemplate rabbitTemplate;
    private final PilacoinService pilacoinService;

    @Value("${queue.pilacoin.minerado}")
    private String pilaMineradoQueue;

    public MineracaoService(DifficultService difficultService, CryptoUtil cryptoUtil, RabbitTemplate rabbitTemplate,
            PilacoinService pilacoinService) {
        this.difficultService = difficultService;
        this.cryptoUtil = cryptoUtil;
        this.rabbitTemplate = rabbitTemplate;
        this.pilacoinService = pilacoinService;
    }

    @PostConstruct
    public void minerar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (difficultService.difficultJson == null) {
                            Thread.sleep(3000);

                            continue;
                        }

                        System.out.println("\n\n[MINING PILACOIN]");

                        DifficultJson difficultJson = difficultService.difficultJson;
                        BigInteger dificuldade = new BigInteger(difficultJson.getDificuldade(),
                                16).abs();

                        PilaCoinJson pilaJson = PilaCoinJson.builder()
                                .nomeCriador("Gabriel_Valentim")
                                .dataCriacao(new Date())
                                .chaveCriador(cryptoUtil.generateKeys().getPublic().getEncoded()).build();

                        byte[] bNum = new byte[256 / 8];
                        Random random = new Random(System.currentTimeMillis());

                        do {
                            random.nextBytes(bNum);
                            pilaJson.setNonce(new BigInteger(bNum).abs().toString());
                        } while (cryptoUtil.generatehash(pilaJson).compareTo(dificuldade) > 0);

                        if (difficultJson.getValidadeFinal().compareTo(new Date()) > 0 || true) {
                            ObjectMapper mapper = new ObjectMapper();
                            String pilaStr = mapper.writeValueAsString(pilaJson);

                            pilacoinService.save(pilaJson, PilaCoin.StatusPila.AG_VALIDACAO);

                            System.out.println("\n\n[MINED PILA]: " + pilaStr);

                            rabbitTemplate.convertAndSend(pilaMineradoQueue, pilaStr);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
