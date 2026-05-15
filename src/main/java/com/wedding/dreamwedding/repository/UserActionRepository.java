package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.UserAction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepository extends MongoRepository<UserAction, String> {
}
