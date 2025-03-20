package no.cerca.services;

import no.cerca.entities.Client;
import no.cerca.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by jadae on 12.03.2025
 */
@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public Optional<Client> findByPhone(String phone) {
        return clientRepository.findByPhone(phone);
    }

    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
}