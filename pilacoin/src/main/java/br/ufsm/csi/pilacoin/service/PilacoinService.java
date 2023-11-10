package br.ufsm.csi.pilacoin.service;

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

  public PilaCoin save(PilaCoinJson pilaCoinJson) {

    PilaCoin pilaCoin = PilaCoin.builder()
        .chaveCriador(pilaCoinJson.getChaveCriador())
        .dataCriacao(pilaCoinJson.getDataCriacao())
        .nomeCriador(pilaCoinJson.getNomeCriador())
        .nonce(pilaCoinJson.getNonce())
        .status(PilaCoin.StatusPila.VALIDO)
        .build();

    return pRepository.save(pilaCoin);
  }
}