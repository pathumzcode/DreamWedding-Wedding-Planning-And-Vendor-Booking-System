package com.wedding.dreamwedding.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "samples")
public class sample {
    @Id
    private String id;
    private String name;


}
