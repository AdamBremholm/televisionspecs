package org.ia.televisionspecs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    //Populates with mock data
    @GetMapping("/manytelevisions")
    public List<Television> addFromData() {

        String jsonReviews = "{\"televisions\":[" +
                "    {" +
                "      \"model\": \"LG OLED55C8\"," +
                "      \"price\": 12990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\" " +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE55Q7FN\"," +
                "      \"price\": 11790," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG OLED65C8\"," +
                "      \"price\": 21990," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE55NU6035\"," +
                "      \"price\": 5900," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE65Q9FN\"," +
                "      \"price\": 24614," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE75NU8005\"," +
                "      \"price\": 19990," +
                "      \"specs\":{\"size\": \"75\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE65Q8FN\"," +
                "      \"price\": 20990," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE49Q6FN\"," +
                "      \"price\": 8790," +
                "      \"specs\":{\"size\": \"49\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE65Q6FN\"," +
                "      \"price\": 13990," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Sony Bravia KD-65XF9005\"," +
                "      \"price\": 15290," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE65Q7FN\"," +
                "      \"price\": 15990," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE82Q6FN\"," +
                "      \"price\": 29990," +
                "      \"specs\":{\"size\": \"82\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 32PFS6402\"," +
                "      \"price\": 3429," +
                "      \"specs\":{\"size\": \"32\"," +
                "      \"resolution\": \"1920x1080\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE55Q9FN\"," +
                "      \"price\": 17990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE65NU6025\"," +
                "      \"price\": 8890," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Panasonic TX-55FZ800E\"," +
                "      \"price\": 13990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE55NU7305\"," +
                "      \"price\": 5990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 43PFT5503\"," +
                "      \"price\": 2990," +
                "      \"specs\":{\"size\": \"43\"," +
                "      \"resolution\": \"1920x1080\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Sony Bravia KD-55XF9005\"," +
                "      \"price\": 10490," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Toshiba 49U6763DB\"," +
                "      \"price\": 3799," +
                "      \"specs\":{\"size\": \"49\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Sony Bravia KD-65AF9\"," +
                "      \"price\": 29990," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE75Q9FN\"," +
                "      \"price\": 41990," +
                "      \"specs\":{\"size\": \"75\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Panasonic TX-55FX780E\"," +
                "      \"price\": 10389," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG OLED65E8\"," +
                "      \"price\": 24990," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE55NU7105\"," +
                "      \"price\": 5990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG OLED55B8\"," +
                "      \"price\": 12748," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 65OLED873\"," +
                "      \"price\": 26206," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE75Q8DN\"," +
                "      \"price\": 28990," +
                "      \"specs\":{\"size\": \"75\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG OLED65B8\"," +
                "      \"price\": 20767," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE65NU8005\"," +
                "      \"price\": 11990," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 55PUS8303\"," +
                "      \"price\": 9831," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 55PUS6803\"," +
                "      \"price\": 5990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE55Q8DN\"," +
                "      \"price\": 13990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE65NU8045\"," +
                "      \"price\": 12900," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 75PUS7803\"," +
                "      \"price\": 17990," +
                "      \"specs\":{\"size\": \"75\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE49NU7105\"," +
                "      \"price\": 5890," +
                "      \"specs\":{\"size\": \"49\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 43PUS6703\"," +
                "      \"price\": 4103," +
                "      \"specs\":{\"size\": \"43\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 55OLED803\"," +
                "      \"price\": 14990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG 70UK6950\"," +
                "      \"price\": 9990," +
                "      \"specs\":{\"size\": \"70\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Sony Bravia KD-65AF8\"," +
                "      \"price\": 24890," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG 65SK8500\"," +
                "      \"price\": 13732," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG 55UK6300\"," +
                "      \"price\": 5238," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung QE55Q6FN\"," +
                "      \"price\": 10378," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Hitachi 55HL9000G\"," +
                "      \"price\": 9990," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Samsung UE50NU6025\"," +
                "      \"price\": 5490," +
                "      \"specs\":{\"size\": \"50\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Sony Bravia KD-49XF9005\"," +
                "      \"price\": 9990," +
                "      \"specs\":{\"size\": \"49\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG 55UK6400\"," +
                "      \"price\": 5468," +
                "      \"specs\":{\"size\": \"55\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"Philips 65OLED903\"," +
                "      \"price\": 28990," +
                "      \"specs\":{\"size\": \"65\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}," +
                "    {" +
                "      \"model\": \"LG OLED77C8\"," +
                "      \"price\": 61251," +
                "      \"specs\":{\"size\": \"77\"," +
                "      \"resolution\": \"3840x2160\"" +
                "    }}" +
                "\t]}";

        ObjectMapper mapper = new ObjectMapper();
        TelevisionWrapper wrapper;

        try {
            wrapper = mapper.readValue(jsonReviews, TelevisionWrapper.class);
            wrapper.televisions.stream().forEach( review -> storage.save(review));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storage.findAll();
    }

}
