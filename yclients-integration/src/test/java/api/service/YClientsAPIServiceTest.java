package api.service;

import no.cerca.api.YClientsAPIService;
import no.cerca.api.client.YClientsAPIClient;
import no.cerca.api.response.CommonAPIResponse;
import no.cerca.dtos.basic.AuthDTO;
import no.cerca.dtos.exchange.RequestAuthDTO;
import no.cerca.dtos.exchange.ResponseDTO;
import no.cerca.entities.Auth;
import no.cerca.services.AuthService;
import no.cerca.services.ClientService;
import no.cerca.services.RecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by jadae on 19.03.2025
 */
class YClientsAPIServiceTest {

    @Mock
    private YClientsAPIClient apiClient;

    @Mock
    private ClientService clientService;

    @Mock
    private AuthService authService;

    @Mock
    private RecordService recordService;

    @InjectMocks
    private YClientsAPIService yClientsAPIService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Пользователь уже авторизован")
    void testAuthenticateUserAlreadyAuthorized() {
        RequestAuthDTO requestAuth = new RequestAuthDTO("testLogin", "testPassword", "testToken");
        Auth existingAuth = new Auth();

        when(authService.getByLogin(requestAuth.getLogin())).thenReturn(Optional.of(existingAuth));

        CommonAPIResponse<Auth> response = yClientsAPIService.authenticateUser(requestAuth);

        assertEquals("success", response.getStatus());
        assertEquals("Пользователь уже авторизован", response.getMessage());
        assertNotNull(response.getData());

        verify(authService, never()).save(any());
        verify(apiClient, never()).auth(any());
    }

    @Test
    void authenticateUser_WhenUserAlreadyAuthenticated_ReturnsSuccess() {
        RequestAuthDTO requestAuth = new RequestAuthDTO("test_user", "password", "partner");
        Auth existingAuth = new Auth();
        existingAuth.setLogin("test_user");

        when(authService.getByLogin(requestAuth.getLogin())).thenReturn(Optional.of(existingAuth));

        CommonAPIResponse<Auth> response = yClientsAPIService.authenticateUser(requestAuth);

        assertEquals("success", response.getStatus());
        assertEquals(existingAuth, response.getData());
        assertEquals("Пользователь уже авторизован", response.getMessage());
    }

    @Test
    void authenticateUser_WhenNewUser_CallsApiAndSavesAuth() {
        RequestAuthDTO requestAuth = new RequestAuthDTO("new_user", "password", "partner");
        AuthDTO authDTO = new AuthDTO();
        authDTO.setLogin("new_user");
        ResponseDTO<AuthDTO> authResponse = new ResponseDTO<>(true, authDTO, null);

        when(authService.getByLogin(requestAuth.getLogin())).thenReturn(Optional.empty());
        when(apiClient.auth(requestAuth)).thenReturn(authResponse);
        when(authService.save(any(Auth.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommonAPIResponse<Auth> response = yClientsAPIService.authenticateUser(requestAuth);

        assertEquals("success", response.getStatus());
        assertNotNull(response.getData());
        assertEquals("Успешное выполнение авторизации пользователя через API YClients", response.getMessage());
    }

    @Test
    void deleteRecord_WhenAuthNotFound_ReturnsError() {
        Long authId = 1L;
        Long recordId = 100L;

        when(authService.get(authId)).thenReturn(Optional.empty());

        CommonAPIResponse<Void> response = yClientsAPIService.deleteRecord(authId, recordId);

        assertEquals("error", response.getStatus());
        assertEquals("Авторизация не найдена", response.getMessage());
    }

    @Test
    void deleteRecord_WhenApiCallSucceeds_ReturnsSuccess() {
        Long authId = 1L;
        Long recordId = 100L;
        Auth auth = new Auth();
        auth.setCompanyId(123L);
        auth.setUserToken("token");

        when(authService.get(authId)).thenReturn(Optional.of(auth));
        doNothing().when(apiClient).deleteRecord(auth.getCompanyId(), recordId, auth.getUserToken());

        CommonAPIResponse<Void> response = yClientsAPIService.deleteRecord(authId, recordId);

        assertEquals("success", response.getStatus());
        assertEquals("Запись успешно удалена", response.getMessage());
    }

    @Test
    void notifyClientsAboutRecordUpdate_SendsNotificationsSuccessfully() {
        Long authId = 1L;
        List<Long> clientIds = List.of(1L, 2L);
        Auth auth = new Auth();
        auth.setCompanyId(123L);
        auth.setUserToken("token");

        when(authService.get(authId)).thenReturn(Optional.of(auth));
        when(apiClient.sendCustomSms(anyLong(), any(), anyString())).thenReturn(new ResponseDTO<>(true, null, null));
        when(apiClient.sendCustomEmail(anyLong(), any(), anyString())).thenReturn(new ResponseDTO<>(true, null, null));

        CommonAPIResponse<Void> response = yClientsAPIService.notifyClientsAboutRecordUpdate(authId, clientIds);

        assertEquals("success", response.getStatus());
        assertEquals("Уведомления успешно отправлены", response.getMessage());
    }

    @Test
    void notifyClientsAboutRecordUpdate_WhenSmsFails_ReturnsError() {
        Long authId = 1L;
        List<Long> clientIds = List.of(1L, 2L);
        Auth auth = new Auth();
        auth.setCompanyId(123L);
        auth.setUserToken("token");

        when(authService.get(authId)).thenReturn(Optional.of(auth));
        when(apiClient.sendCustomSms(anyLong(), any(), anyString())).thenReturn(new ResponseDTO<>(false, null, null));

        CommonAPIResponse<Void> response = yClientsAPIService.notifyClientsAboutRecordUpdate(authId, clientIds);

        assertEquals("error", response.getStatus());
        assertEquals("Не удалось отправить SMS уведомление", response.getMessage());
    }
}
