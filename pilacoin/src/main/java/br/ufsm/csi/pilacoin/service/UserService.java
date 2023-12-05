package br.ufsm.csi.pilacoin.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufsm.csi.pilacoin.constant.QueryConstant;
import br.ufsm.csi.pilacoin.constant.UserConstant;
import br.ufsm.csi.pilacoin.model.json.QueryJson;

@Service
public class UserService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${queue.query}")
    private String query;

    @Value("${queue.user.query}")
    private String userQuery;

    public UserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public ResponseEntity<Object> findAll() {
        QueryJson queryJson = QueryJson.builder()
                .idQuery(QueryConstant.ID_QUERY_USER)
                .nomeUsuario(UserConstant.USERNAME)
                .tipoQuery(QueryJson.TypeQuery.USUARIOS)
                .build();

        QueryJson queryJsonResponse = findAllByQuery(queryJson);

        if (queryJsonResponse == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(queryJsonResponse.getUsuariosResult(), HttpStatus.OK);
    }

    public QueryJson findAllByQuery(QueryJson queryJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            rabbitTemplate.convertAndSend(query, mapper.writeValueAsString(queryJson));

            for (int tries = 0; tries <= 10; tries++) {
                String queryResponse = (String) this.rabbitTemplate.receiveAndConvert(userQuery);

                if (queryResponse == null) {
                    Thread.sleep(1000);

                    continue;
                }

                QueryJson queryJsonResponse = mapper.readValue(queryResponse, QueryJson.class);

                if (queryJsonResponse.getIdQuery() == QueryConstant.ID_QUERY_USER) {
                    return queryJsonResponse;
                }
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
