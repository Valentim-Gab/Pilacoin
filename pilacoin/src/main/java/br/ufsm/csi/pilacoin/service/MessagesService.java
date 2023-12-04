package br.ufsm.csi.pilacoin.service;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.User;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.json.QueryJson;
import br.ufsm.csi.pilacoin.model.json.ReportJson;

// @Service
public class MessagesService {
  public List<User> userList = new ArrayList<>();
  private final Long typeQueryUser = 1l;
  private final Long typeQueryPila = 2l;
  private final PilacoinService pilacoinService;
  private SimpMessagingTemplate template;

  public MessagesService(PilacoinService pilacoinService, SimpMessagingTemplate template) {
    this.pilacoinService = pilacoinService;
    this.template = template;
  }

  @RabbitListener(queues = { "${queue.user}" })
  public void getMessagesUser(@Payload String message) {
    try {
      System.out.println("\n[MESSAGE]: " + message);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @RabbitListener(queues = { "${queue.user.query}" })
  public void responseQueryUser(@Payload String strJson) {
    try {
      ObjectMapper om = new ObjectMapper();
      QueryJson queryJson = om.readValue(strJson, QueryJson.class);

      if (queryJson.getIdQuery() == null) {
        return;
      } else if (queryJson.getIdQuery() == typeQueryUser) {
        userList = queryJson.getUsuariosResult();
      } else if (queryJson.getIdQuery() == typeQueryPila) {
        for (PilaCoinJson pilacoin : queryJson.getPilasResult()) {
          if (pilacoin.getNomeCriador().equals("Gabriel_Valentim")) {
            pilacoinService.save(pilacoin);
          }
        }

        template.convertAndSend("/topic/data", pilacoinService.findAllCount());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @RabbitListener(queues = { "${queue.report}" })
  public void getReportUser(@Payload String message) {
    try {
      if (message == null) {
        Thread.sleep(1000);
      }

      ObjectMapper om = new ObjectMapper();
      List<ReportJson> reportJsonList = om.readValue(message, new TypeReference<List<ReportJson>>() {
      });

      for (ReportJson reportJson : reportJsonList) {
        if (reportJson.getNomeUsuario() != null && reportJson.getNomeUsuario().equals("Gabriel_Valentim")) {
          System.out.println("\n\n[REPORT]: " + reportJson);
        }
      }

      Thread.sleep(1000);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
