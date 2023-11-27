package br.ufsm.csi.pilacoin.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.json.ValidationPilaCoinJson;

@Service
public class MessagesService {

  @RabbitListener(queues = { "${queue.user}" })
  public void getMessagesUser(@Payload String message) {
    try {
      System.out.println(message);
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
