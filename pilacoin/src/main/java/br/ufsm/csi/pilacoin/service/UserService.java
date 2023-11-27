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
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.json.QueryJson;

@Service
public class UserService {
  @Value("${queue.query}")
  private String query;

  private RabbitTemplate rabbitTemplate;

  public UserService(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public List<User> findAll() {
    List<User> userList = new ArrayList<>();

    // userList.add(new User(null, "chave publica aqui".getBytes(), "joao_oli"));
    // userList.add(new User(null, "chave publica aqui".getBytes(), "Luiz"));
    // userList.add(new User(null, "chave publica aqui".getBytes(), "londeroedu"));
    // userList.add(new User(null, "chave publica aqui".getBytes(), "joao_leo"));

    findAllByQuery();

    return userList;
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

  public void findAllByQuery() {
    try {
      ObjectMapper om = new ObjectMapper();

      QueryJson queryJson = QueryJson.builder()
          .idQuery(1l)
          .nomeUsuario("Gabriel_Valentim")
          .tipoQuery("USUARIOS")
          .build();

      rabbitTemplate.convertAndSend(query, om.writeValueAsString(queryJson));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @RabbitListener(queues = { "${queue.user.query}" })
  public void showUsersQuery(@Payload String strJson) {
    try {
      // ObjectMapper om = new ObjectMapper();
      // QueryJson queryJson = om.readValue(strJson, QueryJson.class);

      // System.out.println("\n\n[USERS]: " + queryJson.getUsuariosResult());
      System.out.println(strJson);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
