package org.ia.televisionspecs;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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
    @GetMapping("/reviews")
    public Review getReviews() {
        Review review = restTemplate.getForObject("http://192.168.1.166:65444/reviews/1", Review.class);
        System.out.println(review.toString());
        return review;
    }

}
