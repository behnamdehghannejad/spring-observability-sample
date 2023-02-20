package com.example.springobservabiltysample;

import io.micrometer.observation.annotation.Observed;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Observed(name = "save-user")
public class SampleService {

    private final UserRepository repository;

    @Observed(name = "save-user", contextualName = "user#save",
            lowCardinalityKeyValues = { "abc", "123", "test", "42" })
    public void saveUser(Users users) {
        repository.save(users);
    }
}
