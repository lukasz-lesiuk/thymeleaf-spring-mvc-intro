package com.raczkowski.springintro.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubContributorsDto {

    @JsonSetter("login")
    private String login;

    @JsonProperty("contributions")
    private Integer contributions;

    public GithubContributorsDto() {
    }

    public String getLogin() {
        return login;
    }

    public Integer getContributions() {
        return contributions;
    }
}
