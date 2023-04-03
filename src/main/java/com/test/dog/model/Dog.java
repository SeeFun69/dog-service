package com.test.dog.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dogs")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Field cannot be blank")
    private String name;

    @NotBlank(message = "Field cannot be blank")
    private String breed;

    private int age;

    @NotBlank(message = "Field cannot be blank")
    private String color;

}

