package br.ufsm.csi.pilacoin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsm.csi.pilacoin.model.User;
import br.ufsm.csi.pilacoin.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping()
  public List<User> findAll() {
    return userService.findAll();
  }

  @GetMapping("{name}")
  public ResponseEntity<Object> findOne(@PathVariable("name") String name) {
    return userService.findOne(name);
  }
}
