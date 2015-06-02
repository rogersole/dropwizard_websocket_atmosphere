package com.rogersole.example.dropwizard_atmosphere.dto;

import io.dropwizard.jackson.JsonSnakeCase;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Data Transfer Object that maps an Error. It's the object returned (converted to JSON) on the
 * responses when something faile (an exception is raised) in the endpoints.
 * 
 * @author rogersole
 *
 */

@JsonSnakeCase
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"error", "message"})
public class ResponseErrorDTO {
    @JsonProperty
    private String error   = null;

    @JsonProperty
    private String message = null;

    public ResponseErrorDTO(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
