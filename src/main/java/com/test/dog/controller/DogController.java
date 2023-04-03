package com.test.dog.controller;

import com.test.dog.model.Dog;
import com.test.dog.service.DogApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dogs")
public class DogController {

    @Autowired
    private DogApiService dogApiService;

    public DogController(DogApiService dogApiService) {
        this.dogApiService = dogApiService;
    }

    @GetMapping("/dog-breeds")
    public ResponseEntity<Map<String, List<String>>> getDogBreeds() {
        return ResponseEntity.ok(dogApiService.getDogBreeds());
    }

    @GetMapping("/dog-breeds/{breed}")
    public List<String> getDogSubBreeds(@PathVariable String breed) {
        return dogApiService.getDogSubBreeds(breed);
    }

    @GetMapping("/{id}")
    public Dog findById(@PathVariable Long id) {
        return dogApiService.getDogById(id);
    }

    @PostMapping
    public Dog save(@RequestBody Dog dog) {
        return dogApiService.createDog(dog);
    }

    @PutMapping("/{id}")
    public Dog update(@PathVariable Long id, @RequestBody Dog dog) {
        return dogApiService.updateDog(id, dog);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        dogApiService.deleteDog(id);
    }

}

