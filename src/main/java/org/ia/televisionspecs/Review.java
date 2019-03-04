package org.ia.televisionspecs;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class Review {

    private Long id;
    private String model;
    private String review;
    private float rating;
    private String author;

}
