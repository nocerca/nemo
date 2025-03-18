package no.cerca.api;

import no.cerca.api.client.YClientsAPIClient;
import no.cerca.api.response.CommonAPIResponse;
import no.cerca.dtos.basic.RecordDTO;
import no.cerca.dtos.exchange.RequestRecordsDTO;
import no.cerca.dtos.exchange.ResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jadae on 18.03.2025
 */
@Component
public class YClientsService {

    private final YClientsAPIClient yClientsAPIClient;

    public YClientsService(YClientsAPIClient yClientsAPIClient) {
        this.yClientsAPIClient = yClientsAPIClient;
    }

    public CommonAPIResponse<List<RecordDTO>> getUserRecords(Long clientId, String userToken, Integer page, Integer count) {
        RequestRecordsDTO requestDTO = new RequestRecordsDTO();
        requestDTO.setClientId(clientId);
        requestDTO.setPage(page);
        requestDTO.setCount(count);

        try {
            ResponseDTO<List<RecordDTO>> response = yClientsAPIClient.getRecords(clientId, requestDTO, userToken);

            return new CommonAPIResponse<>("success", response.getData());
        } catch (Exception e) {
            return new CommonAPIResponse<>("error", null);
        }
    }
}
