package com.pie.demo.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getLibroInesistenteRestituisce404() throws Exception {
        mockMvc.perform(get("/book/getBook/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Libro non trovato con id 999"));
    }

    void createEmptyFieldsBookWithError400() throws Exception {
        String body = """
                {"title": "", "author": ""}
                """;

        mockMvc.perform(
                post("/book/newBook").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Il titolo è obbligatorio"))
                .andExpect(jsonPath("$.errors.author").value("L'autore è obbligatorio"));
    }

    @Test
    void addNewBookWith200OK() throws Exception {
        String body = """
                {"title": "Se questo è un uomo", "author": "Primo Levi"}
                """;

        mockMvc.perform(
                post("/book/newBook").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Se questo è un uomo"))
                .andExpect(jsonPath("$.id").exists());
    }
}