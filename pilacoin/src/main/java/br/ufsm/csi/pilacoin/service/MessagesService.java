package br.ufsm.csi.pilacoin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.User;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.json.QueryJson;
import br.ufsm.csi.pilacoin.model.json.ValidationPilaCoinJson;

@Service
public class MessagesService {
  public List<User> userList = new ArrayList<>();
  public PilaCoinJson pilaCoinJsonToUpdate;
  private final Long typeQueryUser = 1l;
  private final Long typeQueryPila = 2l;

  @RabbitListener(queues = { "${queue.user}" })
  public void getMessagesUser(@Payload String message) {
    try {
      System.out.println(message);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @RabbitListener(queues = { "${queue.user.query}" })
  public void showUsersQuery(@Payload String strJson) {
    try {
      ObjectMapper om = new ObjectMapper();
      QueryJson queryJson = om.readValue(strJson, QueryJson.class);

      if (queryJson.getIdQuery() == null)
        return;
      else if (queryJson.getIdQuery() == typeQueryUser)
        userList = queryJson.getUsuariosResult();
      // else if (queryJson.getIdQuery() == typeQueryPila)
      // pilaCoinJsonToUpdate = queryJson.getPilasResult().get(0);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // @RabbitListener(queues = { "${queue.report}" })
  public void getReportUser(@Payload String message) {
    try {
      System.out.println(message);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // @RabbitListener(queues = { "${queue.pilacoin.valided}" })
  public void getValidedPila(@Payload String message) {
    try {
      System.out.println(message);

      ObjectMapper om = new ObjectMapper();
      ValidationPilaCoinJson pilaCoinValided = null;
      pilaCoinValided = om.readValue(message, ValidationPilaCoinJson.class);

      if (pilaCoinValided.getNomeValidador().contains("Gabriel Valentim")) {
        System.out.println(pilaCoinValided.toString());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
