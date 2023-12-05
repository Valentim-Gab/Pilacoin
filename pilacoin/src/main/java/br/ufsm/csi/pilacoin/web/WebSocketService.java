package br.ufsm.csi.pilacoin.web;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.json.TypeActionWsJson;

@Service
public class WebSocketService {
    private SimpMessagingTemplate template;
    private TypeActionWsJson typeActionWsJson;

    public WebSocketService(SimpMessagingTemplate template, ObjectMapper mapper) {
        this.template = template;
        typeActionWsJson = null;
    }

    public void send(String message, String dest) {
        template.convertAndSend(dest, message);
    }

    public void send(Long message, String dest) {
        template.convertAndSend(dest, message);
    }

    public void send(String message, String dest, TypeActionWsJson.TypeAction typeAction) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            this.typeActionWsJson = TypeActionWsJson.builder()
                    .message(message)
                    .type(typeAction)
                    .timestamp(System.currentTimeMillis())
                    .build();

            template.convertAndSend(dest, mapper.writeValueAsString(typeActionWsJson));
        } catch (JsonProcessingException e) {
            System.out.println("\n\n[ERROR]: erro ao criar JSON");
        }
    }
}
