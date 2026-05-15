package com.wedding.dreamwedding.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "site_settings")
public class SiteSettings {
    @Id
    private String id = "global";
    private String phone;
    private String email;
    private String facebook;
    private String instagram;
    private String whatsapp;
    private String address;
}
