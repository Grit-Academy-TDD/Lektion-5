package org.example;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    //When user exists in GitHub, return 200 response code
    @Test
    public void givenUserDoesExist_then200CodeIsTheResponse() throws IOException {
        //Given
        String name = "omar";
        HttpGet request = new HttpGet("https://api.github.com/users/" + name);

        //When
        CloseableHttpResponse closeableHttpResponse =
                HttpClientBuilder.create().build().execute(request);

        //Then
        assertEquals(HttpStatus.SC_OK, closeableHttpResponse.getStatusLine().getStatusCode());
    }

    //When user does not exist in GitHub, return 404 response code
    @Test
    public void givenUserDoesNotExist_then404CodeIsTheResponse() throws IOException {
        //Given
        String name = "seogtou32lj4b3l24";
        HttpGet request = new HttpGet("https://api.github.com/users/" + name);

        //When
        CloseableHttpResponse closeableHttpResponse =
                HttpClientBuilder.create().build().execute(request);

        //Then
        assertEquals(HttpStatus.SC_NOT_FOUND, closeableHttpResponse.getStatusLine().getStatusCode());
    }

    //When request is given with no accept header, the default payload should be in JSON format
    @Test
    public void givenRequestWithNoAcceptHeader_thenDefaultResponseContentTypeIsJSON() throws IOException {
        //Given
        String name = "omar";
        String contentType = "application/json";
        HttpGet request = new HttpGet("https://api.github.com/users/" + name);

        //When
        CloseableHttpResponse closeableHttpResponse =
                HttpClientBuilder.create().build().execute(request);
        String actualContentType = ContentType.getOrDefault(closeableHttpResponse.getEntity()).getMimeType();

        //Then
        assertEquals(contentType, actualContentType);
    }

    //When request is given with video accept header, the default payload should be not in JSON format
    @Test
    public void givenRequestWithAcceptHeaderVideo_thenDefaultResponseContentTypeShouldNotBeOK() throws IOException {
        //Given
        String name = "omar";
        String contentType = "application/video";
        // HttpGet request = new HttpGet("https://api.github.com/users/" + name);
        HttpUriRequest request = RequestBuilder
                .get()
                .setUri("https://api.github.com/users/" + name)
                .setHeader(HttpHeaders.CONTENT_TYPE, contentType)
                .build();

        //When
        CloseableHttpResponse closeableHttpResponse =
                HttpClientBuilder.create().build().execute(request);
        String actualContentType = ContentType.getOrDefault(closeableHttpResponse.getEntity()).getMimeType();

        //Then
        assertNotEquals(contentType, actualContentType);
    }

    //When data is fetched, the data should be correct
    @Test
    public void givenUserDoesExist_thenDataShouldBeCorrect() throws IOException {
        //Given
        String name = "safer";
        HttpGet request = new HttpGet("https://api.github.com/users/" + name);

        //When
        CloseableHttpResponse closeableHttpResponse =
                HttpClientBuilder.create().build().execute(request);

        String jsonFromResponse = EntityUtils.toString(closeableHttpResponse.getEntity());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = mapper.readValue(jsonFromResponse, User.class);
        //System.out.println(user.getBio());

        //Then
        assertEquals(name, user.getLogin());
    }

}