package com.raczkowski.springintro.github.controller;

import com.raczkowski.springintro.github.client.async.GithubAsyncHttpClient;
import com.raczkowski.springintro.github.client.sync.GithubSyncHttpClient;
import com.raczkowski.springintro.github.dto.GithubContributorsDto;
import com.raczkowski.springintro.github.dto.GithubRepositoryDto;
import com.raczkowski.springintro.github.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class RepositoryController {

    private GithubAsyncHttpClient githubAsyncHttpClient;
    private GithubSyncHttpClient githubSyncHttpClient;

    public RepositoryController(GithubAsyncHttpClient githubAsyncHttpClient,
                                GithubSyncHttpClient githubSyncHttpClient) {
        this.githubAsyncHttpClient = githubAsyncHttpClient;
        this.githubSyncHttpClient = githubSyncHttpClient;
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundExceptions(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), NOT_FOUND);
    }

    @GetMapping("/repositories/{owner}/{repositoryName}")
    public GithubRepositoryDto getRepository(@PathVariable String owner,
                                             @PathVariable String repositoryName) throws InterruptedException, IOException, URISyntaxException {
        return githubSyncHttpClient.getGithubRepository(owner, repositoryName);
    }

    @GetMapping("/repositories/{owner}/{repositoryName}/contributors")
    public List<GithubContributorsDto> getContributors(@PathVariable String owner,
                                                       @PathVariable String repositoryName) throws InterruptedException, IOException, URISyntaxException {
        return githubSyncHttpClient.getGithubContributorsDto(owner, repositoryName);
    }

    @DeleteMapping("/repositories/{owner}/{repositoryName}")
    public Integer delete(@PathVariable String owner,
                                       @PathVariable String repositoryName) throws InterruptedException, IOException, URISyntaxException {
        return githubSyncHttpClient.deleteGithubRepository(owner, repositoryName).statusCode();
    }

}
