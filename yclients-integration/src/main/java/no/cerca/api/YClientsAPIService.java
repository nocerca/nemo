package no.cerca.api;

import jakarta.transaction.Transactional;
import no.cerca.api.client.YClientsAPIClient;
import no.cerca.api.response.CommonAPIResponse;
import no.cerca.dtos.basic.AuthDTO;
import no.cerca.dtos.exchange.RequestAuthDTO;
import no.cerca.dtos.exchange.ResponseDTO;
import no.cerca.entities.Auth;
import no.cerca.services.AuthService;
import no.cerca.services.ClientService;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by jadae on 18.03.2025
 */
@Component
public class YClientsAPIService {

    private final YClientsAPIClient apiClient;
    private final ClientService clientService;
    private final AuthService authService;

    public YClientsAPIService(YClientsAPIClient apiClient, ClientService clientService, AuthService authService) {
        this.apiClient = apiClient;
        this.clientService = clientService;
        this.authService = authService;
    }

    @Transactional
    public CommonAPIResponse<Auth> authenticateUser(RequestAuthDTO requestAuth, String partnerToken) {
        Optional<Auth> authOptional = authService.getByLogin(requestAuth.getLogin());
        Auth authToReturn = authOptional.orElse(null);

        String message;
        if (authToReturn == null) {

            try {
                ResponseDTO<AuthDTO> authResponse = apiClient.auth(requestAuth, partnerToken);
                Auth authToSave = new Auth(authResponse.getData());
                authToReturn = authService.save(authToSave);
                message = "Успешное выполнение авторизации пользователя через API YClients";
            } catch (Exception e) {
                //logger
                message = "Ошибка авторизации: " + e.getMessage();
            }

        } else {
            message = "Пользователь уже авторизован";
        }

        CommonAPIResponse<Auth> commonAPIResponse;
        if (authToReturn != null) {
            commonAPIResponse = new CommonAPIResponse<>("success", authToReturn, message);
        } else {
            commonAPIResponse = new CommonAPIResponse<>("error", null, message);
        }

        return commonAPIResponse;
    }
}
