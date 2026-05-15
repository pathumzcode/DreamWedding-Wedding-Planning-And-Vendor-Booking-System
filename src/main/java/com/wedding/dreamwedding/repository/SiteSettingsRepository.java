package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.SiteSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SiteSettingsRepository extends MongoRepository<SiteSettings, String> {
}
