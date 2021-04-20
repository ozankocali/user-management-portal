package com.ozeeesoftware.usermanagementportal.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS=5;
    public static final int ATTEMPT_INCREMENT=1;
    private LoadingCache<String,Integer> loggingAttemptCache;

    public LoginAttemptService() {

        super();
        loggingAttemptCache= CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });

    }

    public void evictUserFromLoginAttemptCache(String username){

        loggingAttemptCache.invalidate(username);

    }

    public void addUserToLoggingAttemptCache(String username){

        int attempts=0;
        try {
            attempts=ATTEMPT_INCREMENT + loggingAttemptCache.get(username);

        }catch (ExecutionException exception){
            exception.printStackTrace();

        }
        loggingAttemptCache.put(username,attempts);

    }


    public boolean hasExceededMaxAttempts(String username){
        try {
            return loggingAttemptCache.get(username)>=MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException exception) {
            exception.printStackTrace();
        }
        return false;
    }


}
