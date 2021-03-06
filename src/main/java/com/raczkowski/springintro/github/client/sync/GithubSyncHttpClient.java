package com.raczkowski.springintro.github.client.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raczkowski.springintro.github.configuration.ApplicationProperties;
import com.raczkowski.springintro.github.dto.GithubContributorsDto;
import com.raczkowski.springintro.github.dto.GithubRepositoryDto;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Logger;

@Service
public class GithubSyncHttpClient {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com/repos/%s/%s";
    private static final String GITHUB_API_BASE_URL_CONTRIBUTORS = "https://api.github.com/repos/%s/%s/contributors";
    private static final String GITHUB_V3_MIME_TYPE = "application/vnd.github.v3+json";
    private static final String REPOSITORY_NOT_FOUND_EXCEPTION_MESSAGE = "Repository not found for given name: %s";
    private final ApplicationProperties applicationProperties;
    private final HttpClient httpClient;

    public GithubSyncHttpClient(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public GithubRepositoryDto getGithubRepository(String owner, String repositoryName) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest get = HttpRequest.newBuilder()
                .uri(new URI(String.format(GITHUB_API_BASE_URL, owner, repositoryName)))
                .headers(HttpHeaders.ACCEPT, GITHUB_V3_MIME_TYPE)
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(get, HttpResponse.BodyHandlers.ofString());

        return deserialize(httpResponse.body());
    }

    public List<GithubContributorsDto> getGithubContributorsDto(String owner, String repositoryName) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest get = HttpRequest.newBuilder()
                .uri(new URI(String.format(GITHUB_API_BASE_URL_CONTRIBUTORS, owner, repositoryName)))
                .headers(HttpHeaders.ACCEPT, GITHUB_V3_MIME_TYPE)
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(get, HttpResponse.BodyHandlers.ofString());

        return deserializeContributors(httpResponse.body());
    }

    public HttpResponse<String> deleteGithubRepository(String owner, String repositoryName) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest get = HttpRequest.newBuilder()
                .uri(new URI(String.format(GITHUB_API_BASE_URL, owner, repositoryName)))
                .headers(HttpHeaders.ACCEPT, GITHUB_V3_MIME_TYPE)
                .headers(HttpHeaders.AUTHORIZATION, String.format("token %s", applicationProperties.getGithub().getToken()))
                .DELETE()
                .build();

        return httpClient.send(get, HttpResponse.BodyHandlers.ofString());
    }

    private Authenticator authenticator(String username, String password) {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        };
    }

    private GithubRepositoryDto deserialize(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, GithubRepositoryDto.class);
    }

    private List<GithubContributorsDto> deserializeContributors(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, new TypeReference<List<GithubContributorsDto>>(){});
    }

}
