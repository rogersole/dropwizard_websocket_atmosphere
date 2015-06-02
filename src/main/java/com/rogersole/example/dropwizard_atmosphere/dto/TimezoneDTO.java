package com.rogersole.example.dropwizard_atmosphere.dto;

import io.dropwizard.jackson.JsonSnakeCase;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Data Transfer Object that maps a Timezone. It's the object returned (converted to JSON) on the
 * responses realted with timezone endpoints.
 * 
 * @author rogersole
 *
 */

@JsonSnakeCase
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"timezone", "time"})
public class TimezoneDTO {
    @JsonProperty
    private String timezone = null;

    @JsonProperty
    private String time     = null;

    public TimezoneDTO() {
        // Jackson deserialization
    }

    public TimezoneDTO(String timezone, String time) {
        this.timezone = timezone;
        this.time = time;
    }
}
