package org.ia.televisionspecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

@RestController
public class TelevisionController {

    Storage storage;
    RestTemplate restTemplate;


    public TelevisionController(Storage storage, RestTemplate restTemplate) {

        this.storage = storage;
        this.restTemplate = restTemplate;
    }


    @GetMapping("/televisions")
    public List<Television> getAll(){
        return storage.findAll();
    }

    @GetMapping("/televisions/{id}")
    public Television getOne(@PathVariable Long id){
        return storage.findById(id)
                .orElseThrow( () -> new TelevisionException("No tv with id " + id));
    }

    @PostMapping("/televisions")
    public Television create(@RequestBody Television television ) {
        return storage.save(television);
    }

    @DeleteMapping("/televisions/{id}")
    public void delete(@PathVariable Long id){
        storage.deleteById(id);
    }

    @PutMapping("/televisions/{id}")
    public Television change(@RequestBody Television product, @PathVariable Long id){
        return storage.findById(id).map(storedTelevision -> {
            storedTelevision.setModel(product.getModel());
            storedTelevision.setSpecs(product.getSpecs());
            return storage.save(storedTelevision);
        }).orElseThrow( () -> new TelevisionException("No tv with id " + id));
    }
    @GetMapping("/getreviews")
    public Review getReviews() {
        //String accesstoken = "";

      //  accesstoken = getAccessToken("user", "password");

       /* HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accesstoken);
        HttpEntity<?> httpEntity = new HttpEntity<Object>(headers);
        ResponseEntity<List<Review>> responseEntity = restTemplate.exchange(
        "http://localhost:8080/reviews/",
        HttpMethod.GET,
        httpEntity,
        new ParameterizedTypeReference<List<Review>>() {
        });*/

        Review review = restTemplate.getForObject("http://review-app/reviews/1", Review.class);
        System.out.println(review);
        return review;

        //System.out.println(responseEntity.getBody().toString());
      //  return responseEntity.getBody();
    }

    public String getAccessToken(String username, String password) {

        String accessToken ="";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<String>("{\"username\": \""+username+"\",\"password\": \""+password+"\"}", headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/auth", HttpMethod.POST, entity, String.class);
        System.out.println("Response: " + response.toString());
        accessToken = response.getHeaders().get("Authorization").toString();
        System.out.println(accessToken);

        String[] parts = accessToken.split(" ");

        accessToken = removeLastCharacter(parts[1]); //removes last ] from string

        System.out.println("Final access token: " + accessToken);
        return accessToken;

    }
    public String removeLastCharacter(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ']') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    @PostMapping("/manytelevisions")
    public List<Television> addFromData(@RequestBody TelevisionWrapper televisionWrapper) {

        televisionWrapper.televisions.stream().forEach( television -> storage.save(television));

        return storage.findAll();
    }

}
