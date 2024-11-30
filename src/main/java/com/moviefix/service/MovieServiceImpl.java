package com.moviefix.service;

import com.moviefix.dto.MovieDto;
import com.moviefix.dto.MoviePageResponse;
import com.moviefix.entity.Movie;
import com.moviefix.exceptions.FileExistsException;
import com.moviefix.exceptions.MovieNotFoundException;
import com.moviefix.respository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file)
            throws IOException {

        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File exists already! Please upload with another file name!");
        }

        String fileName = fileService.uploadFile(path, file);

        movieDto.setPoster(fileName);

        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        Movie savedMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + fileName;

        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDto getMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId));

        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        return getMovieDtoObject(movies);
    }

    @Override
    public MovieDto updateMovie(MovieDto movieDto, MultipartFile file, Long movieId) throws IOException {
        Movie movieFind = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId));

        String fileName = movieFind.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        movieDto.setPoster(fileName);

        Movie movie = new Movie(
                movieFind.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        movieRepository.save(movie);

        String posterUrl = baseUrl + "/file/" + fileName;

        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public String deleteMovie(Long movieId) throws IOException{
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId));

        Files.deleteIfExists(Paths.get(path + "/file/" + movie.getPoster()));

        movieRepository.delete(movie);
        return "Movie deleted with id = " + movieId;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<Movie> movies = moviePage.getContent();
        List<MovieDto> movieDtoList = getMovieDtoObject(movies);

        return new MoviePageResponse(movieDtoList, pageNumber, pageSize, moviePage.getTotalElements(), moviePage.getTotalPages(), moviePage.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<Movie> movies = moviePage.getContent();
        List<MovieDto> movieDtoList = getMovieDtoObject(movies);

        return new MoviePageResponse(movieDtoList, pageNumber,
                pageSize, moviePage.getTotalElements(),
                moviePage.getTotalPages(), moviePage.isLast());
    }

    private List<MovieDto> getMovieDtoObject(List<Movie> movies) {
        List<MovieDto> movieDtoList = new ArrayList<>();

        for (Movie i : movies) {
            String posterUrl = baseUrl + "/file/" + i.getPoster();
            MovieDto response = new MovieDto(
                    i.getMovieId(),
                    i.getTitle(),
                    i.getDirector(),
                    i.getStudio(),
                    i.getMovieCast(),
                    i.getReleaseYear(),
                    i.getPoster(),
                    posterUrl
            );
            movieDtoList.add(response);
        }
        return movieDtoList;
    }
}
