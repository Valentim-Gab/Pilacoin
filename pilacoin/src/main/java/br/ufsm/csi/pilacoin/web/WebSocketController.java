package br.ufsm.csi.pilacoin.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/update")
public class WebSocketController {

  @SendTo("/topic/data")
  public String update(String message) {
    return message;
  }

  @SendTo("/topic/pilacoin")
  public String pilacoin(String message) {
    return message;
  }
}
