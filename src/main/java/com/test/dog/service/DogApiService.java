package com.test.dog.service;
import com.test.dog.dto.*;
import com.test.dog.handler.DogNotFoundException;
import com.test.dog.model.Dog;
import com.test.dog.repo.DogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DogApiService {

    private static final String DOG_API_URL = "https://dog.ceo/api";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DogRepository dogRepository;

    public DogApiService(RestTemplate restTemplate, DogRepository dogRepository) {
        this.restTemplate = restTemplate;
        this.dogRepository = dogRepository;
    }

    public Map<String, List<String>> getDogBreeds() {
        String url = DOG_API_URL + "/breeds/list/all";
        DogBreedsResponse response = restTemplate.getForObject(url, DogBreedsResponse .class);
        return response.getMessage();
    }

    public List<String> getDogSubBreeds(String breed) {
        String url = DOG_API_URL + "/breed/" + breed + "/list";
        String url1 = DOG_API_URL + "/breed/" + breed + "/images/random/3";
        String url2 = DOG_API_URL + "/breed/" + breed + "/images";
        DogSubBreedsResponse response = restTemplate.getForObject(url, DogSubBreedsResponse.class);
        DogImagesResponse response1 = restTemplate.getForObject(url1, DogImagesResponse.class);
        DogImagesResponse response2 = restTemplate.getForObject(url2, DogImagesResponse.class);

        List<String> resultList = new ArrayList<>();
        List<String> result = response.getMessage();
        List<String> result1 = response1.getMessage();
        List<String> result2 = response2.getMessage();
        List<String> oddNumberedData = null;
        if(breed.equals("sheepdog")){
            result.stream().forEach(temp -> resultList.add(breed.concat("-").concat(temp)));
        } else if (breed.equals("terrier")) {
            result.stream().forEach(temp -> resultList.add(breed.concat("-").concat(temp).concat(" : ").concat(String.valueOf(result1))));
        } else if (breed.equals("shiba")) {
            oddNumberedData = new ArrayList<>();

            for (int i = 0; i < result2.size(); i++) {
                if (i % 2 == 0) {
                    oddNumberedData.add(result2.get(i));
                }
            }
            return oddNumberedData;
        } else {
            result.stream().forEach(temp -> resultList.add(temp));
        }
        return resultList;
    }

    public Dog getDogById(Long id) {
        return dogRepository.findById(id)
                .orElseThrow(() -> new DogNotFoundException(id));
    }

    public Dog createDog(Dog dog) {
        return dogRepository.save(dog);
    }

    public Dog updateDog(Long id, Dog dogDetails) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new DogNotFoundException(id));

        dog.setName(dogDetails.getName());
        dog.setBreed(dogDetails.getBreed());
        dog.setAge(dogDetails.getAge());

        return dogRepository.save(dog);
    }

    public ResponseEntity<?> deleteDog(Long id) {
        Dog dog = dogRepository.findById(id)
                .orElseThrow(() -> new DogNotFoundException(id));

        dogRepository.delete(dog);

        return ResponseEntity.ok().build();
    }
}




