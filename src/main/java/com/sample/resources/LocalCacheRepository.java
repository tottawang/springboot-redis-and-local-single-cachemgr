package com.sample.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.sample.domain.User;

@Repository
public class LocalCacheRepository {

  private LoadingCache<String, List<User>> loadingCache;

  @Autowired
  private LocalCacheComponent localCacheComponent;

  @PostConstruct
  public void init() {
    loadingCache = Caffeine.newBuilder().expireAfterWrite(60000, TimeUnit.MILLISECONDS)
        .build(new CacheLoader<String, List<User>>() {
          @Override
          public List<User> load(String token) throws Exception {
            return getEmptyUsers();
          }
        });
  }

  public List<User> getCachedEmptyUsers() {
    return loadingCache.get("emptyUsers");
  }

  /**
   * Verify query for empty results can be cached as well
   * 
   * @return
   */
  public List<User> getEmptyUsers() {
    localCacheComponent.doSomething();
    List<User> users = new ArrayList<>();
    return users;
  }

  public void evictEmptyUsers() {
    loadingCache.invalidate("emptyUsers");
  }


}
