package com.cosmetics.ecommerce.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenService(
            @Value("${google.client-id}") String clientId) {

        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public GoogleIdToken.Payload verify(String credential)
            throws GeneralSecurityException, IOException {

        GoogleIdToken idToken = verifier.verify(credential);

        if (idToken == null) {
            throw new IllegalArgumentException(
                    "Invalid Google ID token"
            );
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new IllegalArgumentException(
                    "Google email is not verified"
            );
        }

        if (payload.getSubject() == null
                || payload.getEmail() == null) {
            throw new IllegalArgumentException(
                    "Required Google account details are missing"
            );
        }

        return payload;
    }
}
