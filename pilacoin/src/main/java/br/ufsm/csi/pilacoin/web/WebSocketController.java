package br.ufsm.csi.pilacoin.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

  @MessageMapping("/update")
  @SendTo("/topic/data")
  public String update(String message) {
    System.out.println("\n\n[RECEIVED MESSAGE]: " + message);

    return message;
  }
}
