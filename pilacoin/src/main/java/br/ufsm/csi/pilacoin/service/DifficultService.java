package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.json.DifficultJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class DifficultService {
    @Value("${queue.errors}")
    private String queue_errors;

    DifficultJson difficultJson;

    @RabbitListener(queues = {"${queue.dificuldade}"})
    public void receiveDifficult(@Payload String receivedDifficultJson) {
        ObjectMapper om = new ObjectMapper();

        try {
            difficultJson = om.readValue(receivedDifficultJson, DifficultJson.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
