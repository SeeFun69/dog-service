package com.test.dog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.dog.model.Dog;
import com.test.dog.service.DogApiService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(DogController.class)
@AutoConfigureMockMvc
public class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DogApiService dogApiService;

    private static final String BREED = "shiba";
    private static final String SUB_BREED = "inu";
    private static final Map<String, List<String>> DOG_BREEDS = Collections.singletonMap(BREED, Collections.singletonList(SUB_BREED));
    private static final List<String> DOG_SUB_BREEDS = Collections.singletonList(SUB_BREED);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getDogBreeds_shouldReturnMapOfDogBreeds() throws Exception {
        given(dogApiService.getDogBreeds()).willReturn(DOG_BREEDS);

        mockMvc.perform(get("/dogs/dog-breeds"))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(DOG_BREEDS)));
    }

    @Test
    public void getDogSubBreeds_shouldReturnListOfDogSubBreeds() throws Exception {
        given(dogApiService.getDogSubBreeds(BREED)).willReturn(DOG_SUB_BREEDS);

        mockMvc.perform(get("/dogs/dog-breeds/{breed}", BREED))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(DOG_SUB_BREEDS)));
    }

    @Test
    public void testFindById() throws Exception {
        Long id = 1L;
        Dog dog = new Dog();
        dog.setId(id);
        dog.setName("Buddy");
        given(dogApiService.getDogById(id)).willReturn(dog);

        mockMvc.perform(get("/dogs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Buddy"));
    }

    @Test
    public void testSave() throws Exception {
        Long id = 1L;
        Dog dog = new Dog();
        dog.setId(id);
        dog.setName("Buddy");
        given(dogApiService.createDog(dog)).willReturn(dog);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/dogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dog));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.breed").doesNotExist());
    }

    @Test
    public void updateTest() throws Exception {
        Long id = 1L;
        Dog dog = new Dog();
        dog.setId(id);
        dog.setName("Buddy");

        given(dogApiService.updateDog(id, dog)).willReturn(dog);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/dogs/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dog));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.breed").doesNotExist());
    }

    @Test
    public void testDeleteById() throws Exception {
        Long id = 1L;
        mockMvc.perform(delete("/dogs/{id}", id))
                .andExpect(status().isOk());
        verify(dogApiService, times(1)).deleteDog(id);
    }
}


