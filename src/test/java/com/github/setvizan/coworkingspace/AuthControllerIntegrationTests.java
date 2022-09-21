package com.github.setvizan.coworkingspace;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.setvizan.coworkingspace.security.JwtServiceHMAC;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerIntegrationTests {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtServiceHMAC jwtService;
    @Autowired
    private MockMvc mockMvc;

    private final UUID memberId = UUID.fromString("5435f12e-1b12-4ee6-bbae-df34323cc154"); // member from db

    @Test
    @Order(1)
    public void getToken() throws Exception {

        MvcResult response = mockMvc.perform(post("/api/auth/token").param("grant_type", "password")
                                                              .param("email", "member@gmail.com")
                                                              .param("password", "password1234"))
                                    .andExpect(status().isOk())
                                    .andDo(print())
                                    .andReturn();

        Map<String, String> map = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<Map<String, String>>() {});
        String token = map.get("access_token");
        mockMvc.perform(get("/api/user/"+this.memberId).header("Authorization", "Bearer " + token))
               .andExpect(status().isOk())
               .andDo(print())
               .andReturn();
    }
}
