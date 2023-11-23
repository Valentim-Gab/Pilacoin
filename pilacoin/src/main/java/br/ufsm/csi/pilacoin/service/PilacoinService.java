package br.ufsm.csi.pilacoin.service;

import java.util.Optional;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;

@Service
public class PilacoinService {
  private final PilaCoinRepository pRepository;

  public PilacoinService(PilaCoinRepository pRepository) {
    this.pRepository = pRepository;
  }

  public PilaCoin save(PilaCoinJson pilaCoinJson, PilaCoin.StatusPila status) {
    PilaCoin pilaCoin = PilaCoin.builder()
        .chaveCriador(pilaCoinJson.getChaveCriador())
        .dataCriacao(pilaCoinJson.getDataCriacao())
        .nomeCriador(pilaCoinJson.getNomeCriador())
        .nonce(pilaCoinJson.getNonce())
        .status(status)
        .build();

    return pRepository.save(pilaCoin);
  }

  public List<PilaCoin> findAll() {
    return pRepository.findAll();
  }

  public ResponseEntity<Object> findOneByNonce(String nonce) {
    try {
      Optional<PilaCoin> pilaCoin = pRepository.findPilaCoinByNonce(nonce);

      if (pilaCoin.isPresent()) {
        return new ResponseEntity<>(pilaCoin.get(), HttpStatus.OK);
      } else {
        Map<String, Object> jsonData = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        jsonData.put("error", "Pilacoin não encontrado");

        return new ResponseEntity<>(objectMapper.writeValueAsString(jsonData), HttpStatus.BAD_REQUEST);
      }
    } catch (JsonProcessingException e) {
      return new ResponseEntity<>("Erro no servidor", HttpStatus.BAD_REQUEST);
    }
  }

  public void update(PilaCoinJson pilaCoinJson, PilaCoin.StatusPila status) {
    Optional<PilaCoin> pilaCoin = pRepository.findPilaCoinByNonce(pilaCoinJson.getNonce());

    if (pilaCoin.isPresent()) {
      pilaCoin.get().setStatus(status);
      pRepository.save(pilaCoin.get());
    } else {
      System.out.println("\n\n[ERROR]: PilaCoin não salvo no Banco");
    }
  }
}
