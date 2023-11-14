package br.ufsm.csi.pilacoin.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

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
