package com.medialistmaker.movie.service.movie;

import com.medialistmaker.movie.connector.omdb.OmdbConnectorProxy;
import com.medialistmaker.movie.domain.Movie;
import com.medialistmaker.movie.dto.externalapi.omdbapi.item.MovieElementDTO;
import com.medialistmaker.movie.exception.badrequestexception.CustomBadRequestException;
import com.medialistmaker.movie.exception.notfoundexception.CustomNotFoundException;
import com.medialistmaker.movie.exception.servicenotavailableexception.ServiceNotAvailableException;
import com.medialistmaker.movie.repository.MovieRepository;
import com.medialistmaker.movie.utils.CustomEntityValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final ModelMapper modelMapper;

    private final MovieRepository movieRepository;

    private final OmdbConnectorProxy omdbConnectorProxy;

    private final CustomEntityValidator<Movie> movieValidator;

    public MovieServiceImpl(
            ModelMapper modelMapper,
            MovieRepository movieRepository,
            OmdbConnectorProxy omdbConnectorProxy,
            CustomEntityValidator<Movie> movieValidator
    ) {
        this.modelMapper = modelMapper;
        this.movieRepository = movieRepository;
        this.omdbConnectorProxy = omdbConnectorProxy;
        this.movieValidator = movieValidator;
    }

    @Override
    public List<Movie> browseByIds(List<Long> movieIds) {
        List<Movie> movieList =  this.movieRepository.getByIds(movieIds);

        movieList.sort(Comparator.comparingInt(movie -> movieIds.indexOf(movie.getId())));

        return movieList;
    }

    @Override
    public Movie readById(Long movieId) throws CustomNotFoundException {

        Optional<Movie> movie = this.movieRepository.findById(movieId);

        if (movie.isEmpty()) {
            log.error("Movie with id {} not found", movieId);
            throw new CustomNotFoundException("Not found");
        }

        return movie.get();

    }

    @Override
    public Movie readByApiCode(String apiCode) throws CustomNotFoundException {

        Movie movie = this.movieRepository.getByApiCode(apiCode);

        if (isNull(movie)) {
            log.error("Movie with api code {} not found", apiCode);
            throw new CustomNotFoundException("NOT FOUND");
        }

        return movie;
    }

    @Override
    public Movie addByApiCode(String apiCode) throws CustomBadRequestException, ServiceNotAvailableException {

        Movie isMovieAlreadyExist = this.movieRepository.getByApiCode(apiCode);

        if(nonNull(isMovieAlreadyExist)) {
            return isMovieAlreadyExist;
        }

        MovieElementDTO movieElementDTO = this.omdbConnectorProxy.getByApiCode(apiCode);

        if(isNull(movieElementDTO)) {
            throw new CustomBadRequestException("Movie not exists");
        }

        Movie movie = this.modelMapper.map(movieElementDTO, Movie.class);

        return this.add(movie);
    }

    private Movie add(Movie movie) throws CustomBadRequestException {

        List<String> movieErrors = this.movieValidator.validateEntity(movie);

        if (Boolean.FALSE.equals(movieErrors.isEmpty())) {
            log.error("Movie not valid: {}", movieErrors);
            throw new CustomBadRequestException("Bad request", movieErrors);
        }

        return this.movieRepository.save(movie);
    }

    @Override
    public Movie deleteById(Long movieId) throws CustomNotFoundException {

        Optional<Movie> movie = this.movieRepository.findById(movieId);

        if (movie.isEmpty()) {
            log.error("Error on deleting movie with id {}: Movie not exists", movieId);
            throw new CustomNotFoundException("Not found");
        }

        this.movieRepository.delete(movie.get());

        return movie.get();

    }
}
