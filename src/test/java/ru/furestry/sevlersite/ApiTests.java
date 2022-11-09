package ru.furestry.sevlersite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiTests {

    @Autowired
    private MockMvc mockMvc;

    private final String apiUrl = "/api/v1/";

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    public void pingReturnOk() throws Exception {
        mockMvc.perform(get(apiUrl + "ping"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK")));
    }

    @Test
    public void usersReturnList() throws Exception {
        mockMvc.perform(get(apiUrl + "users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void usersByIdReturnOk() throws Exception {
        mockMvc.perform(get(apiUrl + "users/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":2,\"username\":\"Test\"}")));
    }

    @Test
    public void usersByIdReturnNotFound() throws Exception {
        mockMvc.perform(get(apiUrl + "users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void usersMeReturnOk() throws Exception {
        mockMvc.perform(get(apiUrl + "users/me")
                        .header("Authorization", "23a7f64b354c87c39124f8e5e198974c38a4736b6c5a76e657482c46908cdeb6")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":2,\"username\":\"Test\"}")));
    }

    @Test
    public void usersMeReturnUnauthorized() throws Exception {
        mockMvc.perform(
                get(apiUrl + "users/me")
                        .header("Authorization", "")
                )
                .andExpect(status().isUnauthorized());
    }

}
