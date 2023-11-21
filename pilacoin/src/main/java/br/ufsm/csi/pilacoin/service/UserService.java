package br.ufsm.csi.pilacoin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.User;

@Service
public class UserService {
  public List<User> findAll() {
    List<User> userList = new ArrayList<>();

    userList.add(new User(null, "chave publica aqui".getBytes(), "joao_oli"));
    userList.add(new User(null, "chave publica aqui".getBytes(), "Luiz"));
    userList.add(new User(null, "chave publica aqui".getBytes(), "londeroedu"));
    userList.add(new User(null, "chave publica aqui".getBytes(), "joao_leo"));

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

  @RabbitListener(queues = { "${queue.users}" })
  public void getMessagesUser(@Payload String message) {
    try {
      System.out.println(message);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
