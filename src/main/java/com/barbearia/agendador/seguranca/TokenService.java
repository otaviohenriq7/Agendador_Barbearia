package com.barbearia.agendador.seguranca;

import com.barbearia.agendador.modelo.Usuario;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    private static final long VALIDADE_EM_HORAS = 8;

    private final SecretKey chave;

    public TokenService(@Value("${agendador.jwt.secret}") String segredo) {
        this.chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();

        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(VALIDADE_EM_HORAS, ChronoUnit.HOURS)))
                .signWith(chave)
                .compact();
    }

    public String extrairLogin(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
