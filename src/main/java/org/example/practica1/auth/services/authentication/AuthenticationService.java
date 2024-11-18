package org.example.practica1.auth.services.authentication;


import org.example.practica1.auth.dto.JwtAuthResponse;
import org.example.practica1.auth.dto.UserSignInRequest;
import org.example.practica1.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}