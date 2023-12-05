package br.ufsm.csi.pilacoin.service;

import java.util.Optional;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import br.ufsm.csi.pilacoin.model.json.QueryJson;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.constant.QueryConstant;
import br.ufsm.csi.pilacoin.constant.UserConstant;
import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.json.PilaCoinJson;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.web.WebSocketService;

@Service
public class PilacoinService {
    private final PilaCoinRepository pRepository;
    private final RabbitTemplate rabbitTemplate;
    private final WebSocketService webSocketService;

    @Value("${queue.query}")
    private String query;

    @Value("${queue.user.query}")
    private String userQuery;

    @Value("${queue.transfer}")
    private String transactionQueue;

    public PilacoinService(PilaCoinRepository pRepository, RabbitTemplate rabbitTemplate,
            WebSocketService webSocketService) {
        this.pRepository = pRepository;

        this.rabbitTemplate = rabbitTemplate;
        this.webSocketService = webSocketService;
    }

    public PilaCoin save(PilaCoinJson pilaCoinJson) {
        Optional<PilaCoin> existingPilaCoin = pRepository.findPilaCoinByNonce(pilaCoinJson.getNonce());
        PilaCoin pilacoin = null;

        if (existingPilaCoin.isPresent()) {
            if (existingPilaCoin.get().getStatus() == pilaCoinJson.getStatus()) {
                return existingPilaCoin.get();
            }

            pilacoin = existingPilaCoin.get();
            pilacoin.setStatus(pilaCoinJson.getStatus());
        } else {
            pilacoin = PilaCoin.builder()
                    .chaveCriador(pilaCoinJson.getChaveCriador())
                    .dataCriacao(pilaCoinJson.getDataCriacao())
                    .nomeCriador(pilaCoinJson.getNomeCriador())
                    .nonce(pilaCoinJson.getNonce())
                    .status(pilaCoinJson.getStatus())
                    .build();
        }

        return pRepository.save(pilacoin);
    }

    public List<PilaCoin> findAll() {
        return pRepository.findAll();
    }

    public Long findAllCount() {
        return pRepository.count();
    }

    @PostConstruct
    public void emitAllCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        webSocketService.send(findAllCount(), "/topic/data");

                        Thread.sleep(5000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ResponseEntity<Object> findOneByNonce(String nonce) {
        Optional<PilaCoin> pilaCoin = pRepository.findPilaCoinByNonce(nonce);

        try {
            if (pilaCoin.isPresent()) {
                return new ResponseEntity<>(pilaCoin.get(), HttpStatus.OK);
            } else {
                Map<String, Object> jsonData = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();

                jsonData.put("error", "Pilacoin não encontrado");

                return new ResponseEntity<>(mapper.writeValueAsString(jsonData), HttpStatus.BAD_REQUEST);
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

    @PostConstruct
    public void updateDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        QueryJson queryJson = QueryJson.builder()
                                .idQuery(QueryConstant.ID_QUERY_PILA)
                                .nomeUsuario(UserConstant.USERNAME)
                                .tipoQuery(QueryJson.TypeQuery.PILA)
                                .build();

                        ObjectMapper mapper = new ObjectMapper();

                        rabbitTemplate.convertAndSend(query, mapper.writeValueAsString(queryJson));

                        for (int tries = 0; tries <= 10; tries++) {
                            String queryResponse = (String) rabbitTemplate.receiveAndConvert(userQuery);

                            if (queryResponse == null) {
                                Thread.sleep(1000);

                                continue;
                            }

                            QueryJson queryJsonResponse = mapper.readValue(queryResponse, QueryJson.class);

                            if (queryJsonResponse.getIdQuery() == QueryConstant.ID_QUERY_PILA
                                    && queryJsonResponse.getPilasResult() != null) {
                                for (PilaCoinJson pilacoin : queryJsonResponse.getPilasResult()) {
                                    if (pilacoin.getNomeCriador().equals(UserConstant.USERNAME)) {
                                        save(pilacoin);
                                    }
                                }
                            }
                        }

                        Thread.sleep(10000);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void deleteByNonce(String nonce) {
        Optional<PilaCoin> pilaCoin = pRepository.findPilaCoinByNonce(nonce);

        if (pilaCoin.isPresent()) {
            pRepository.delete(pilaCoin.get());
        } else {
            System.out.println("\n\n[ERROR]: PilaCoin não salvo no Banco");
        }
    }
}
