package br.ufsm.csi.pilacoin.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/update")
public class WebSocketController {

  @SendTo("/topic/data")
  public String update(String message) {
    System.out.println("\n\n[RECEIVED MESSAGE]: " + message);

    return message;
  }

  @MessageMapping("/update")
  @SendTo("/topic/pilacoin")
  public String pilacoin(String message) {
    System.out.println("\n\n[RECEIVED MESSAGE]: " + message);

    return message;
  }
}
