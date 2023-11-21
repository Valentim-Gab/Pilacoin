package br.ufsm.csi.pilacoin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.service.PilacoinService;

@RestController
@RequestMapping("/pilacoin")
public class PilacoinController {
  private final PilacoinService pilacoinService;

  public PilacoinController(PilacoinService pilacoinService) {
    this.pilacoinService = pilacoinService;
  }

  @GetMapping()
  public List<PilaCoin> findAll() {
    return pilacoinService.findAll();
  }

  @GetMapping("{nonce}")
  public ResponseEntity<Object> findOneByNonce(@PathVariable("nonce") String nonce) {
    return pilacoinService.findOneByNonce(nonce);
  }
}
