package com.test.dog.service;

import com.test.dog.dto.DogBreedsResponse;
import com.test.dog.dto.DogImagesResponse;
import com.test.dog.dto.DogSubBreedsResponse;
import com.test.dog.handler.DogNotFoundException;
import com.test.dog.model.Dog;
import com.test.dog.repo.DogRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class DogApiServiceTest {

    private static final String DOG_API_URL = "https://dog.ceo/api";
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DogRepository dogRepository;

    @InjectMocks
    private DogApiService dogApiService;

    @Test
    public void testGetDogBreeds() {
        // prepare test data
        Map<String, List<String>> message = new HashMap<>();
        List<String> breeds = new ArrayList<>();
        breeds.add("breed1");
        breeds.add("breed2");
        message.put("key", breeds);
        DogBreedsResponse response = new DogBreedsResponse();
        response.setMessage(message);

        // mock RestTemplate behavior
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(DogBreedsResponse.class)))
                .thenReturn(response);

        // test method
        Map<String, List<String>> result = dogApiService.getDogBreeds();

        // assert result
        Assert.assertEquals(message, result);
    }

    @Test
    public void testGetDogById() {
        // prepare test data
        Long id = 1L;
        Dog dog = new Dog(id, "Buddy", "Golden Retriever", 5, "black");

        // mock behavior
        Mockito.when(dogRepository.findById(id)).thenReturn(Optional.of(dog));

        // test method
        Dog result = dogApiService.getDogById(id);

        // assert result
        Assert.assertEquals(dog, result);
    }

    @Test(expected = DogNotFoundException.class)
    public void testGetDogByIdNotFound() {
        // prepare test data
        Long id = 1L;

        // mock behavior
        Mockito.when(dogRepository.findById(id)).thenReturn(Optional.empty());

        // test method
        dogApiService.getDogById(id);
    }

    @Test
    public void testCreateDog() {
        // prepare test data
        Dog dog = new Dog(null, "Buddy", "Golden Retriever", 5, "black");

        // mock behavior
        Mockito.when(dogRepository.save(dog)).thenReturn(dog);

        // test method
        Dog result = dogApiService.createDog(dog);

        // assert result
        Assert.assertEquals(dog, result);
    }

    @Test
    public void testUpdateDog() {
        // prepare test data
        Long id = 1L;
        Dog dog = new Dog(id, "Buddy", "Golden Retriever", 5, "black");
        Dog updatedDog = new Dog(id, "Max", "Labrador Retriever", 6, "black");

        // mock behavior
        Mockito.when(dogRepository.findById(id)).thenReturn(Optional.of(dog));
        Mockito.when(dogRepository.save(dog)).thenReturn(updatedDog);

        // test method
        Dog result = dogApiService.updateDog(id, updatedDog);

        // assert result
        Assert.assertEquals(updatedDog, result);
    }

    @Test(expected = DogNotFoundException.class)
    public void testUpdateDogNotFound() {
        // prepare test data
        Long id = 1L;
        Dog updatedDog = new Dog(id, "Max", "Labrador Retriever", 6, "black");

        // mock behavior
        Mockito.when(dogRepository.findById(id)).thenReturn(Optional.empty());

        // test method
        dogApiService.updateDog(id, updatedDog);
    }

    @Test
    public void testDeleteDog() {
        // prepare test data
        Long id = 1L;
        Dog dog = new Dog(id, "Buddy", "Golden Retriever", 5, "black");

        // mock behavior
        Mockito.when(dogRepository.findById(id)).thenReturn(Optional.of(dog));

        // test method
        ResponseEntity<?> result = dogApiService.deleteDog(id);

        // assert result
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
        Mockito.verify(dogRepository, Mockito.times(1)).delete(dog);
    }

    @Test(expected = DogNotFoundException.class)
    public void testDeleteDogNotFound() {
        // prepare test data
        Long id = 1L;

        // mock behavior
        Mockito.when(dogRepository.findById(id)).thenReturn(Optional.empty());

        // test method
        dogApiService.deleteDog(id);
    }

    @Test
    public void testGetDogSubBreeds() {
        String breed = "sheepdog";
        String subBreed1 = "english";
        String subBreed2 = "scotch";
        List<String> subBreeds = Arrays.asList(subBreed1, subBreed2);

        String url1 = DOG_API_URL + "/breed/" + breed + "/list";
        String url2 = DOG_API_URL + "/breed/" + breed + "/images/random/3";
        String url3 = DOG_API_URL + "/breed/" + breed + "/images";

        DogSubBreedsResponse subBreedsResponse = new DogSubBreedsResponse(subBreeds);
        DogImagesResponse imagesResponse1 = new DogImagesResponse(Collections.emptyList());
        DogImagesResponse imagesResponse2 = new DogImagesResponse(Arrays.asList("https://test.com/image1.jpg", "https://test.com/image2.jpg", "https://test.com/image3.jpg"));

        Mockito.when(restTemplate.getForObject(url1, DogSubBreedsResponse.class))
                .thenReturn(subBreedsResponse);
        Mockito.when(restTemplate.getForObject(url2, DogImagesResponse.class))
                .thenReturn(imagesResponse1);
        Mockito.when(restTemplate.getForObject(url3, DogImagesResponse.class))
                .thenReturn(imagesResponse2);

        List<String> result = dogApiService.getDogSubBreeds(breed);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("sheepdog-english", result.get(0));
        assertEquals("sheepdog-scotch", result.get(1));
    }

    @Test
    public void testGetDogSubBreedsTerrierAndShiba() {
        // Set up mock response data
        List<String> subBreeds = Arrays.asList("sub1", "sub2");
        List<String> images = Arrays.asList("image1", "image2", "image3");

        // Set up mock behavior for RestTemplate
        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/terrier/list", DogSubBreedsResponse.class))
                .thenReturn(new DogSubBreedsResponse(subBreeds));
        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/terrier/images/random/3", DogImagesResponse.class))
                .thenReturn(new DogImagesResponse(images));
        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/terrier/images", DogImagesResponse.class))
                .thenReturn(new DogImagesResponse(images));
        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/shiba/list", DogSubBreedsResponse.class))
                .thenReturn(new DogSubBreedsResponse(subBreeds));
        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/shiba/images/random/3", DogImagesResponse.class))
                .thenReturn(new DogImagesResponse(images));
        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/shiba/images", DogImagesResponse.class))
                .thenReturn(new DogImagesResponse(images));

        // Test for terrier breed
        List<String> result1 = dogApiService.getDogSubBreeds("terrier");
        assertEquals(2, result1.size());
        assertTrue(result1.contains("terrier-sub1 : " + images));
        assertTrue(result1.contains("terrier-sub2 : " + images));

        // Test for shiba breed
        List<String> result2 = dogApiService.getDogSubBreeds("shiba");
        assertEquals(2, result2.size());
        assertEquals("image1", result2.get(0));
        assertEquals("image3", result2.get(1));
    }


//    @Test
//    public void testGetDogSubBreeds() {
//        // prepare test data
//        String breed1 = "sheepdog";
//        String breed2 = "terrier";
//        String breed3 = "shiba";
//        String breed4 = "labrador";
//
//        List<String> subBreeds = new ArrayList<>();
//        subBreeds.add("subbreed1");
//        subBreeds.add("subbreed2");
//
//        List<String> images = new ArrayList<>();
//        images.add("image1");
//        images.add("image2");
//        images.add("image3");
//
//        // mock RestTemplate behavior
//        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/" + breed1 + "/list", DogSubBreedsResponse.class))
//                .thenReturn(new DogSubBreedsResponse(subBreeds));
//        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/" + breed2 + "/list", DogSubBreedsResponse.class))
//                .thenReturn(new DogSubBreedsResponse(new ArrayList<>()));
//        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/" + breed3 + "/list", DogSubBreedsResponse.class))
//                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
//        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/" + breed4 + "/list", DogSubBreedsResponse.class))
//                .thenReturn(new DogSubBreedsResponse(subBreeds));
//        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/" + breed2 + "/images/random/3", DogImagesResponse.class))
//                .thenReturn(new DogImagesResponse(images));
//        Mockito.when(restTemplate.getForObject(DOG_API_URL + "/breed/" + breed3 + "/images", DogImagesResponse.class))
//                .thenReturn(new DogImagesResponse(images));
//
//
//        // test method
//        List<String> result1 = dogApiService.getDogSubBreeds(breed1);
//        List<String> result2 = dogApiService.getDogSubBreeds(breed2);
//        List<String> result3 = dogApiService.getDogSubBreeds(breed3);
//        List<String> result4 = dogApiService.getDogSubBreeds(breed4);
//
//        // assert results
//        List<String> expected1 = Arrays.asList("sheepdog-subbreed1", "sheepdog-subbreed2");
//        List<String> expected2 = Arrays.asList("terrier-subbreed1 : [image1, image2, image3]", "terrier-subbreed2 : [image1, image2, image3]");
//        List<String> expected3 = Arrays.asList("image1", "image3");
//        List<String> expected4 = Arrays.asList("subbreed1", "subbreed2");
//
//        Assert.assertEquals(expected1, result1);
//        Assert.assertEquals(expected2, result2);
//        Assert.assertEquals(expected3, result3);
//        Assert.assertEquals(expected4, result4);
//    }

}

