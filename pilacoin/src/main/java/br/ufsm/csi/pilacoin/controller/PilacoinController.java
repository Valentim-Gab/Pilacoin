package br.ufsm.csi.pilacoin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.json.TransactionJson;
import br.ufsm.csi.pilacoin.service.PilacoinService;
import br.ufsm.csi.pilacoin.service.TransferService;

@RestController
@RequestMapping("/pilacoin")
public class PilacoinController {
    private final PilacoinService pilacoinService;
    private final TransferService transferService;

    public PilacoinController(PilacoinService pilacoinService, TransferService transferService) {
        this.pilacoinService = pilacoinService;
        this.transferService = transferService;
    }

    @GetMapping()
    public List<PilaCoin> findAll() {
        return pilacoinService.findAll();
    }

    @GetMapping("{nonce}")
    public ResponseEntity<Object> findOneByNonce(@PathVariable("nonce") String nonce) {
        return pilacoinService.findOneByNonce(nonce);
    }

    @PostMapping("transfer")
    public void transferOne(@RequestBody TransactionJson transactionJson) {
        transferService.transfer(transactionJson);
    }
}
