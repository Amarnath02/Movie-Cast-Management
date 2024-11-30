package com.moviefix.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MovieDto {

    private Long movieId;

    @NotBlank(message = "This field shouldn't be empty")
    private String title;

    @NotBlank(message = "This field shouldn't be empty")
    private String director;

    @NotBlank(message = "This field shouldn't be empty")
    private String studio;

    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "This field shouldn't be empty")
    private String poster;

    @NotBlank(message = "This field shouldn't be empty")
    private String posterUrl;
}
