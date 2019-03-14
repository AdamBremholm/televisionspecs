package org.ia.televisionspecs;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashMap;

@Data
@Entity
public class Television {


    private @Id @GeneratedValue Long id;
    private String model;
    private HashMap<String, String> specs;
    private float price;
}

