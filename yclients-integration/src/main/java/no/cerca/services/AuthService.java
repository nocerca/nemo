package no.cerca.services;

import no.cerca.entities.Auth;
import no.cerca.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by jadae on 19.03.2025
 */
@Service
public class AuthService {
    @Autowired
    private AuthRepository authRepository;

    public Optional<Auth> getByLogin(String login) {
        return authRepository.findByLogin(login);
    }

    public Auth save(Auth auth){
        return authRepository.save(auth);
    }

    public Optional<Auth> get(Long authId) {
        return authRepository.findById(authId);
    }
}
