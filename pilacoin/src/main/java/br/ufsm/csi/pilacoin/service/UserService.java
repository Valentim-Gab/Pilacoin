package br.ufsm.csi.pilacoin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.User;
import br.ufsm.csi.pilacoin.model.json.QueryJson;

// @Service
public class UserService {
  @Value("${queue.query}")
  private String query;

  @Value("${queue.user.query}")
  private String userQuery;

  private RabbitTemplate rabbitTemplate;

  MessagesService messagesService;

  public UserService(RabbitTemplate rabbitTemplate, MessagesService messagesService) {
    this.rabbitTemplate = rabbitTemplate;
    this.messagesService = messagesService;
  }

  public List<User> findAll() {
    QueryJson queryJson = QueryJson.builder()
        .idQuery(1l)
        .nomeUsuario("Gabriel_Valentim")
        .tipoQuery(QueryJson.TypeQuery.USUARIOS)
        .build();

    return findAllByQuery(queryJson).getUsuariosResult();
  }

  public ResponseEntity<Object> findOne(String name) {
    try {
      User user = new User(null, "chave publica aqui".getBytes(), "joao_oli");

      if (user.getNome().toLowerCase().equals(name.toLowerCase())) {
        return new ResponseEntity<>(user, HttpStatus.OK);
      } else {
        Map<String, Object> jsonData = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        jsonData.put("error", "Usuário não encontrado");

        return new ResponseEntity<>(objectMapper.writeValueAsString(jsonData), HttpStatus.BAD_REQUEST);
      }
    } catch (JsonProcessingException e) {
      return new ResponseEntity<>("Erro no servidor", HttpStatus.BAD_REQUEST);
    }
  }

  public QueryJson findAllByQuery(QueryJson queryJson) {
    try {
      ObjectMapper om = new ObjectMapper();

      rabbitTemplate.convertAndSend(query, om.writeValueAsString(queryJson));

      for (int tries = 0; tries < 10; tries++) {
        String queryResponse = (String) this.rabbitTemplate.receiveAndConvert(userQuery);
        QueryJson queryJsonResponse = om.readValue(queryResponse, QueryJson.class);

        if (queryJsonResponse == null) {
          Thread.sleep(1000);

          continue;
        }

        if (queryJsonResponse.getTipoQuery() == QueryJson.TypeQuery.USUARIOS) {
          return queryJsonResponse;
        }
      }

      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
