package org.example.courzelo.repositories.Groups;

import org.example.courzelo.models.GroupChat.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupMessageRepository extends MongoRepository<Group, String> {
}
