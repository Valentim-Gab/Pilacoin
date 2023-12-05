package br.ufsm.csi.pilacoin.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.json.TransactionJson;
import br.ufsm.csi.pilacoin.utils.CryptoUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private final RabbitTemplate rabbitTemplate;
    private final PilacoinService pilacoinService;

    @Value("${queue.transfer}")
    private String transactionQueue;

    public TransferService(RabbitTemplate rabbitTemplate, PilacoinService pilacoinService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilacoinService = pilacoinService;
    }

    public ResponseEntity<Object> transfer(TransactionJson transaction) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            transaction.setChaveUsuarioOrigem(CryptoUtil.generateKeys().getPublic().getEncoded());
            transaction.setDataTransacao(new Date());
            transaction.setAssinatura(CryptoUtil.generateSignature(transaction));

            System.out.println("\n\n[TRANSFER DESTINATION]: " + transaction.getNomeUsuarioDestino());

            rabbitTemplate.convertAndSend(transactionQueue, mapper.writeValueAsString(transaction));
            pilacoinService.deleteByNonce(transaction.getNoncePila());

            return new ResponseEntity<Object>(transaction, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            e.printStackTrace();

            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
}
