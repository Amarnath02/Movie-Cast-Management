package com.moviefix.service;

import com.moviefix.dto.MovieDto;
import com.moviefix.dto.MoviePageResponse;
import com.moviefix.exceptions.FileExistsException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;
    MovieDto getMovie(Long movieId);
    List<MovieDto> getAllMovies();
    MovieDto updateMovie(MovieDto movieDto, MultipartFile file, Long movieId) throws IOException;
    String deleteMovie(Long movieId) throws IOException;
    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);
    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir);

}
