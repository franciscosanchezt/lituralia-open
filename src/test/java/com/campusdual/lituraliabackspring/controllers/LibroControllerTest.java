package com.campusdual.lituraliabackspring.controllers;

import static com.campusdual.lituraliabackspring.controllers.AbstractRestControllerTest.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.campusdual.lituraliabackspring.api.model.LibroDTO;
import com.campusdual.lituraliabackspring.services.LibroService;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LibroControllerTest {

    public static final String REST_URL = "/libros";
    public static final String HAMLET = "Hamlet";
    public static final String HAMLET_ISBN = "123456";
    @Mock
    LibroService service;

    @InjectMocks
    LibroController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .setControllerAdvice(new RestResponseEntityExceptionHandler())
                                 .build();
    }

    @Test
    void getAllLibros() throws Exception {
        //given
        LibroDTO libro1 = LibroDTO.builder()
                                  .idLibro(1L)
                                  .isbn("123456")
                                  .titulo(HAMLET)
                                  .build();
        LibroDTO libro2 = LibroDTO.builder()
                                  .idLibro(2L)
                                  .isbn("123457")
                                  .titulo("MacBeth")
                                  .build();

        when(service.getAllLibros()).thenReturn(Arrays.asList(libro1, libro2));

        //when
        mockMvc.perform(get(REST_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.libros", hasSize(2)));
    }

    @Test
    void getEmployeeById() throws Exception {
        //given
        LibroDTO libro1 = LibroDTO.builder()
                                  .idLibro(1L)
                                  .isbn(HAMLET_ISBN)
                                  .titulo(HAMLET)
                                  .build();

        when(service.getLibroById(anyLong())).thenReturn(libro1);

        //when
        mockMvc.perform(get(REST_URL + "/1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.titulo", equalTo(HAMLET)))
               .andExpect(jsonPath("$.isbn", equalTo(HAMLET_ISBN)));
    }

    @Test
    void createLibro() throws Exception {
        //given
        LibroDTO libro1 = LibroDTO.builder()
                                  .idLibro(1L)
                                  .isbn(HAMLET_ISBN)
                                  .titulo(HAMLET)
                                  .build();

        LibroDTO returnDTO = LibroDTO.builder()
                                     .idLibro(1L)
                                     .isbn(HAMLET_ISBN)
                                     .titulo(HAMLET)
                                     .build();

        when(service.createLibro(any())).thenReturn(returnDTO);

        //when/then
        mockMvc.perform(post(REST_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(libro1)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.titulo", equalTo(HAMLET)))
               .andExpect(jsonPath("$.isbn", equalTo(HAMLET_ISBN)));
    }

    @Test
    void updateLibro() throws Exception {
        //given
        LibroDTO libro1 = LibroDTO.builder()
                                  .idLibro(1L)
                                  .isbn(HAMLET_ISBN)
                                  .titulo(HAMLET)
                                  .build();

        LibroDTO returnDTO = LibroDTO.builder()
                                     .idLibro(1L)
                                     .isbn(HAMLET_ISBN)
                                     .titulo(HAMLET)
                                     .build();

        when(service.updateLibro(anyLong(), any(LibroDTO.class))).thenReturn(returnDTO);

        //when/then
        mockMvc.perform(put(REST_URL + "/1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(libro1)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.titulo", equalTo(HAMLET)))
               .andExpect(jsonPath("$.isbn", equalTo(HAMLET_ISBN)));
    }

    @Test
    void deleteLibro() throws Exception {
        mockMvc.perform(delete(REST_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());

        verify(service).deleteLibroById(anyLong());
    }

    @Test
    public void testNotFoundException() throws Exception {

        when(service.getLibroById(anyLong())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(REST_URL + "/222")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
}