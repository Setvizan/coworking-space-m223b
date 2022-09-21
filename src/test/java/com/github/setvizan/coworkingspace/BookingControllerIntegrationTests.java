package com.github.setvizan.coworkingspace;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.setvizan.coworkingspace.model.BookingEntity;
import com.github.setvizan.coworkingspace.model.enumerate.BookingType;
import com.github.setvizan.coworkingspace.model.enumerate.Status;
import com.github.setvizan.coworkingspace.security.JwtServiceHMAC;
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

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingControllerIntegrationTests {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtServiceHMAC jwtService;
    @Autowired
    private MockMvc mockMvc;

    private final UUID memberId = UUID.fromString("5435f12e-1b12-4ee6-bbae-df34323cc154"); // member from db
    private final UUID member2Id = UUID.fromString("1234f12e-1b12-4ee6-bbae-df34323cc154"); // other member from db
    private final UUID adminId = UUID.fromString("9135f12e-1b66-4ee6-bbae-df37303cc154"); // admin from db

    @Test
    @Order(1)
    public void getBooking() throws Exception {
        String userToken = jwtService.createNewJWT(UUID.randomUUID().toString(), this.member2Id.toString(), "member", List.of());
        String memberToken = jwtService.createNewJWT(UUID.randomUUID().toString(), this.memberId.toString(), "member", List.of());

        MvcResult responseMember = mockMvc.perform(get("/api/bookings").header("Authorization", "Bearer " + memberToken))
                                          .andExpect(status().isOk())
                                          .andDo(print())
                                          .andReturn();

        List<BookingEntity> bookingsForMember = objectMapper.readValue(responseMember.getResponse().getContentAsString(), new TypeReference<List<BookingEntity>>() {
        });
        assertEquals(bookingsForMember.size(), 1);

        MvcResult responseUser = mockMvc.perform(get("/api/bookings").header("Authorization", "Bearer " + userToken))
                                        .andExpect(status().isOk())
                                        .andDo(print())
                                        .andReturn();

        List<BookingEntity> bookingsForUser = objectMapper.readValue(responseUser.getResponse().getContentAsString(), new TypeReference<List<BookingEntity>>() {
        });
        assertEquals(bookingsForUser.size(), 0);
    }

    @Test
    @Order(2)
    public void updateBooking() throws Exception {
        String adminToken = jwtService.createNewJWT(UUID.randomUUID().toString(), this.adminId.toString(), "admin", List.of("ADMIN"));
        String memberToken = jwtService.createNewJWT(UUID.randomUUID().toString(), this.memberId.toString(), "member", List.of());

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(UUID.fromString("e7e7070f-7dd2-4e57-bbd9-b0cbaac4e8f2"));
        bookingEntity.setStatus(Status.APPROVED);
        bookingEntity.setBookingType(BookingType.DAY);
        bookingEntity.setDate(Date.from(Instant.now()));
        bookingEntity.setMemberId(this.memberId);
        String json = objectMapper.writeValueAsString(bookingEntity);

        mockMvc.perform(put("/api/bookings/e7e7070f-7dd2-4e57-bbd9-b0cbaac4e8f2").contentType(MediaType.APPLICATION_JSON)
                                                         .content(json)
                                                         .characterEncoding("utf-8")
                                                         .header("Authorization", "Bearer " + memberToken)
               )
               .andExpect(status().isForbidden())
               .andDo(print())
               .andReturn();
    }
}
