package com.example.lavnarceburgo.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class PresencaId implements Serializable {

    private Long codaluno;
    private Long codaula;
}