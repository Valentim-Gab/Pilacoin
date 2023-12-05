package br.ufsm.csi.pilacoin.service;

import java.util.Date;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.json.TransactionJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;

public class TransferService {
    private final RabbitTemplate rabbitTemplate;
    private final PilacoinService pilacoinService;

    @Value("${queue.transaction}")
    private String transactionQueue;

    public TransferService(RabbitTemplate rabbitTemplate, PilacoinService pilacoinService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilacoinService = pilacoinService;
    }

    public void transfer(TransactionJson transaction) {
        try {
            transaction.setChaveUsuarioOrigem(CryptoUtil.generateKeys().getPublic().getEncoded());
            transaction.setDataTransacao(new Date());

            ObjectMapper mapper = new ObjectMapper();

            transaction.setAssinatura(CryptoUtil.generateSignature(transaction));
            System.out.println("\n\n[TRANSFER DESTINATION]: " + transaction.getNomeUsuarioDestino());
            rabbitTemplate.convertAndSend(transactionQueue, mapper.writeValueAsString(transaction));
            pilacoinService.deleteByNonce(transaction.getNoncePila());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
