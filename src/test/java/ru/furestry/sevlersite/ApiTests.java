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
        mockMvc.perform(get(apiUrl + "users/21"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":21,\"username\":\"Test\"}")));
    }

    @Test
    public void usersByIdReturnNotFound() throws Exception {
        mockMvc.perform(get(apiUrl + "users/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void usersMeReturnOk() throws Exception {
        mockMvc.perform(get(apiUrl + "users/me")
                        .header("Authorization", "13df7edb91e2e85dd568bbef4c395c4e96e6fdabf909757f2a35246aa9f25b65")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"id\":21,\"username\":\"Test\"}")));
    }

    @Test
    public void usersMeReturnUnauthorized() throws Exception {
        mockMvc.perform(
                get(apiUrl + "users/me")
                        .header("Authorization", "")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void commentsReturnList() throws Exception {
        mockMvc.perform(get(apiUrl + "comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void commentsByIdReturnOk() throws Exception {
        mockMvc.perform(get(apiUrl + "comments/22"))
                .andExpect(status().isOk());
    }

    @Test
    public void commentsByIdReturnNotFound() throws Exception {
        mockMvc.perform(get(apiUrl + "comments/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void commentsByAuthorIdReturnOk() throws Exception {
        mockMvc.perform(get(apiUrl + "comments/user/20"))
                .andExpect(status().isOk());
    }

    @Test
    public void commentsByAuthorIdReturnNotFound() throws Exception {
        mockMvc.perform(get(apiUrl + "comments/user/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void commentsMeReturnOk() throws Exception {
        mockMvc.perform(get(apiUrl + "comments/me")
                        .header("Authorization", "13df7edb91e2e85dd568bbef4c395c4e96e6fdabf909757f2a35246aa9f25b65")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void commentsMeReturnUnauthorized() throws Exception {
        mockMvc.perform(
                        get(apiUrl + "comments/me")
                                .header("Authorization", "")
                )
                .andExpect(status().isUnauthorized());
    }

}
