package com.github.setvizan.coworkingspace;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.setvizan.coworkingspace.model.MemberEntity;
import com.github.setvizan.coworkingspace.security.JwtServiceHMAC;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberControllerIntegrationTests {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtServiceHMAC jwtService;
    @Autowired
    private MockMvc mockMvc;

    private final UUID memberId = UUID.fromString("5435f12e-1b12-4ee6-bbae-df34323cc154"); // member from db
    private final UUID adminId = UUID.fromString("9135f12e-1b66-4ee6-bbae-df37303cc154"); // admin from db

    @Test
    @Order(1)
    public void getUsers() throws Exception {
        String adminToken = jwtService.createNewJWT(UUID.randomUUID().toString(), "9135f12e-1b66-4ee6-bbae-df37303cc154", "admin", List.of("ADMIN"));
        String memberToken = jwtService.createNewJWT(UUID.randomUUID().toString(), this.memberId.toString(), "member", List.of());

        MvcResult response = mockMvc.perform(get("/api/user").header("Authorization", "Bearer " + adminToken))
                                    .andExpect(status().isOk())
                                    .andDo(print())
                                    .andReturn();

        List<MemberEntity> members = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<List<MemberEntity>>() {
        });

        assertEquals(members.size(), 3);

        mockMvc.perform(get("/api/user").header("Authorization", "Bearer " + memberToken))
               .andExpect(status().isForbidden())
               .andDo(print())
               .andReturn();

        mockMvc.perform(get("/api/user/"+this.memberId).header("Authorization", "Bearer " + memberToken))
               .andExpect(status().isOk())
               .andDo(print())
               .andReturn();
    }

    @Test
    @Order(2)
    public void createUser() throws Exception {
        MemberEntity member = new MemberEntity();
        member.setAdmin(false);
        member.setFirstname("test");
        member.setLastname("test");
        member.setPassword("test");
        member.setEmail("test@test.test");
        String json = objectMapper.writeValueAsString(member);

        MvcResult response = mockMvc.perform(post("/api/auth").contentType(MediaType.APPLICATION_JSON)
                                                              .content(json)
                                                              .characterEncoding("utf-8")
                                    )
                                    .andExpect(status().isOk())
                                    .andDo(print())
                                    .andReturn();

        MemberEntity memRes = objectMapper.readValue(response.getResponse().getContentAsString(), MemberEntity.class);

        assertTrue(ObjectUtils.isNotEmpty(memRes));
    }

    @Test
    @Order(3)
    public void updateUser() throws Exception {
        String adminToken = jwtService.createNewJWT(UUID.randomUUID().toString(), this.adminId.toString() , "admin", List.of("ADMIN"));

        MemberEntity updatedMember = new MemberEntity();
        updatedMember.setAdmin(false);
        updatedMember.setFirstname("testU");
        updatedMember.setLastname("testU");
        updatedMember.setPassword("testU");
        updatedMember.setEmail("memberU@gmail.com");
        String json = objectMapper.writeValueAsString(updatedMember);

        MvcResult response = mockMvc.perform(put("/api/user/" + this.memberId)
                                            .header("Authorization", "Bearer " + adminToken)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(json)
                                            .characterEncoding("utf-8")
                                    )
                                    .andExpect(status().isOk())
                                    .andDo(print())
                                    .andReturn();

        MemberEntity testMember = objectMapper.readValue(response.getResponse().getContentAsString(), MemberEntity.class);
        assertEquals(testMember.getEmail(), "memberU@gmail.com");
    }

    @Test
    @Order(4)
    public void deleteUser() throws Exception {
        String adminToken = jwtService.createNewJWT(UUID.randomUUID().toString(), this.adminId.toString(), "admin", List.of("ADMIN"));
        String memberToken = jwtService.createNewJWT(UUID.randomUUID().toString(), this.memberId.toString(), "member", List.of());

        mockMvc.perform(delete("/api/user/"+this.memberId).header("Authorization", "Bearer " + memberToken))
               .andExpect(status().isForbidden())
               .andDo(print())
               .andReturn();

        mockMvc.perform(delete("/api/user/"+this.memberId).header("Authorization", "Bearer " + adminToken))
               .andExpect(status().isOk())
               .andDo(print())
               .andReturn();
    }
}
