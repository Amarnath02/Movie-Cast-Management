package com.moviefix.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviefix.dto.MovieDto;
import com.moviefix.dto.MoviePageResponse;
import com.moviefix.exceptions.EmptyFileException;
import com.moviefix.service.MovieService;
import com.moviefix.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(
            @RequestPart MultipartFile file,
            @RequestPart String movieDtoObj) throws IOException, EmptyFileException {

        if (file == null) {
            throw new EmptyFileException("File is empty! Please send file");
        }

        MovieDto movieDto = convertToJson(movieDtoObj);
        System.out.println(movieDto);
        return new ResponseEntity<>(movieService.addMovie(movieDto, file), HttpStatus.OK);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Long movieId) {
        return new ResponseEntity<>(movieService.getMovie(movieId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler() {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(
            @PathVariable Long movieId,
            @RequestPart MultipartFile file,
            @RequestPart String movieDtoObj
    ) throws IOException {

        if (file.isEmpty()) {
            file = null;
        }

        MovieDto movieDto = convertToJson(movieDtoObj);
        return new ResponseEntity<>(movieService.updateMovie(movieDto, file, movieId), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Long movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    private MovieDto convertToJson(String movieDto)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(movieDto, MovieDto.class);
    }

    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getAllMoviesPaginationHandler(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer PageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer PageSize) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(PageNumber, PageSize));
    }

    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getAllMoviesPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer PageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer PageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String dir
    ) {
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(PageNumber, PageSize, sortBy, dir));
    }
}
