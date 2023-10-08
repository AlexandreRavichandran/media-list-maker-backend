package com.medialistmaker.movie.service.movie.externalapi;

import com.medialistmaker.movie.exception.badrequestexception.CustomBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public abstract class AbstractExternalApiService<T, K> {

    @Autowired
    private RestTemplate restTemplate;

    private HashMap<String, String> parameters;

    public T executeRequestItem() throws RestClientException, CustomBadRequestException {

        try {
            ResponseEntity<T> response = this.restTemplate.getForEntity(this.addUrlParameters(), this.getItemClassType());

            if (Boolean.FALSE.equals(HttpStatus.OK.equals(response.getStatusCode()))) {
                throw new CustomBadRequestException("Bad request", new ArrayList<>());
            }

            return response.getBody();

        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage());
        }


    }

    public K executeRequestCollection() throws RestClientException, CustomBadRequestException {

        try {
            ResponseEntity<K> response = this.restTemplate.getForEntity(this.addUrlParameters(), this.getCollectionClassType());

            if (Boolean.FALSE.equals(HttpStatus.OK.equals(response.getStatusCode()))) {
                throw new CustomBadRequestException("Bad request", new ArrayList<>());
            }

            return response.getBody();

        } catch (RestClientException e) {
            throw new RestClientException(e.getMessage());
        }


    }

    protected void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    protected abstract String getBaseUrl();

    protected abstract String getResourceUrl();

    protected abstract Class<T> getItemClassType();

    protected abstract Class<K> getCollectionClassType();

    private String addUrlParameters() {

        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(this.getFullUrl());

        for (Map.Entry<String, String> param : this.getParameters().entrySet()) {
            uri.queryParam(param.getKey(), param.getValue());
        }

        return uri.toUriString();

    }

    private String getFullUrl() {
        return this.getBaseUrl() + this.getResourceUrl();
    }

    private HashMap<String, String> getParameters() {
        return this.parameters;
    }
}
