package br.ufsm.csi.pilacoin.service;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.json.ReportJson;

@Service
public class MessagesService {
  private final PilacoinService pilacoinService;
  private SimpMessagingTemplate template;
  private final RabbitTemplate rabbitTemplate;

  public MessagesService(PilacoinService pilacoinService, SimpMessagingTemplate template,
                         RabbitTemplate rabbitTemplate) {
    this.pilacoinService = pilacoinService;
    this.template = template;
    this.rabbitTemplate = rabbitTemplate;
  }

  @RabbitListener(queues = { "${queue.user}" })
  public void getMessagesUser(@Payload String message) {
    try {
      System.out.println("\n[MESSAGE]: " + message);
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
