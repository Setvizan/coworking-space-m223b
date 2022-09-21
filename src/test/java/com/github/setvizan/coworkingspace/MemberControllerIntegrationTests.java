package com.github.setvizan.coworkingspace;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.setvizan.coworkingspace.model.MemberEntity;
import com.github.setvizan.coworkingspace.security.JwtServiceHMAC;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerIntegrationTests {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtServiceHMAC jwtService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getUsers() throws Exception {
        String adminToken = jwtService.createNewJWT(UUID.randomUUID().toString(), "9135f12e-1b66-4ee6-bbae-df37303cc154", "admin", List.of("ADMIN"));
        String userToken = jwtService.createNewJWT(UUID.randomUUID().toString(), "5435f12e-1b12-4ee6-bbae-df34323cc154", "user", List.of());

        MvcResult response = mockMvc.perform(get("/api/user").header("Authorization", "Bearer " + adminToken))
                                    .andExpect(status().isOk())
                                    .andDo(print())
                                    .andReturn();

        List<MemberEntity> members = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<List<MemberEntity>>() {});

        assertEquals(members.size(), 2);

        mockMvc.perform(get("/api/user").header("Authorization", "Bearer " + userToken))
                                    .andExpect(status().isForbidden())
                                    .andDo(print())
                                    .andReturn();
    }
}
