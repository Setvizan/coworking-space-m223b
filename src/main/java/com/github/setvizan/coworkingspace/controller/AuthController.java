package com.github.setvizan.coworkingspace.controller;

import com.github.setvizan.coworkingspace.model.MemberEntity;
import com.github.setvizan.coworkingspace.model.TokenResponse;
import com.github.setvizan.coworkingspace.security.JwtServiceHMAC;
import com.github.setvizan.coworkingspace.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.UUID;

/**
 * copied from: https://github.com/viascom/spring-boot-crud-example
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final JwtServiceHMAC jwtService;
    private final MemberService memberService;

    public AuthController(JwtServiceHMAC jwtServiceHMAC, MemberService memberService) {
        this.jwtService = jwtServiceHMAC;
        this.memberService = memberService;
    }

    @Operation(
            summary = "create member",
            description = "allows one to register a new member.",
            tags = {"Registration"}
    )
    @PostMapping
    ResponseEntity<MemberEntity> registerMember(@RequestBody MemberEntity member) {
        String passwordHash = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());
        member.setPassword(passwordHash);
        return ResponseEntity.ok(this.memberService.create(member));
    }

    @Operation(
            summary = "Get new token",
            operationId = "getToken",
            tags = {"Authorization"}
    )
    @PostMapping(value = "/token", produces = "application/json")
    public TokenResponse getToken(
            @Parameter(
                    description = "The grant type which will be used to get an new token",
                    required = true,
                    schema = @Schema(allowableValues = {"password", "refresh_token"})
            )
            @RequestParam(name = "grant_type", required = true)
                    String grantType,
            @Parameter(description = "If refresh_token is selected as grant type this field is needed")
            @RequestParam(name = "refresh_token", required = false)
                    String refreshToken,
            @Parameter(description = "If password is selected as grant type this field is needed", required = false)
            @RequestParam(name = "email", required = false)
                    String email,
            @Parameter(description = "If password is selected as grant type this field is needed", required = false)
            @RequestParam(name = "password", required = false)
                    String password) throws GeneralSecurityException, IOException {

        switch (grantType) {
            case "password" -> {
                MemberEntity member = memberService.oneByEmail(email);

                if (!BCrypt.checkpw(password, member.getPassword())) {
                    throw new IllegalArgumentException("Username or password wrong");
                }

                val id = UUID.randomUUID().toString();
                val scopes = new ArrayList<String>();

                if (member.isAdmin()) {
                    scopes.add("ADMIN");
                }

                val newAccessToken = jwtService.createNewJWT(id, member.getId().toString(), member.getEmail(), scopes);
                val newRefreshToken = jwtService.createNewJWTRefresh(id, member.getId().toString());

                return new TokenResponse(newAccessToken, newRefreshToken, "Bearer", LocalDateTime.now().plusDays(14).toEpochSecond(ZoneOffset.UTC), LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
            }
            case "refresh_token" -> {
                val jwt = jwtService.verifyJwt(refreshToken, false);

                MemberEntity member = memberService.oneById(UUID.fromString(jwt.getClaim("user_id").asString()));

                val id = UUID.randomUUID().toString();
                val scopes = new ArrayList<String>();

                if (member.isAdmin()) {
                    scopes.add("ADMIN");
                }

                val newAccessToken = jwtService.createNewJWT(id, member.getId().toString(), member.getEmail(), scopes);
                val newRefreshToken = jwtService.createNewJWTRefresh(id, member.getId().toString());

                return new TokenResponse(newAccessToken, newRefreshToken, "Bearer", LocalDateTime.now().plusDays(14).toEpochSecond(ZoneOffset.UTC), LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC));
            }
            default -> throw new IllegalArgumentException("Not supported grant type: " + grantType);
        }
    }
}
