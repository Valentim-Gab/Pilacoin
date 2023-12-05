package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.json.DifficultJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class DifficultService {
    private DifficultJson difficultJson;

    @Value("${queue.errors}")
    private String queue_errors;

    @RabbitListener(queues = { "${queue.difficult}" })
    public void receiveDifficult(@Payload String receivedDifficultJson) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            difficultJson = mapper.readValue(receivedDifficultJson, DifficultJson.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private DifficultJson getDifficultJson() {
        try {
            while (true) {
                if (difficultJson == null) {
                    Thread.sleep(500);

                    continue;
                }

                return difficultJson;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getDifficult() {
        return new BigInteger(getDifficultJson().getDificuldade(), 16).abs();
    }

    public Date getFinalValidity() {
        return getDifficultJson().getValidadeFinal();
    }
}
