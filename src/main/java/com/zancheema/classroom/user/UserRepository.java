package com.zancheema.classroom.user;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    public boolean existsByEmail(String email);
}
