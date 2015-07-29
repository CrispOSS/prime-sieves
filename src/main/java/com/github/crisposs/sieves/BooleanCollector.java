package com.github.crisposs.sieves;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BooleanCollector implements Collector {

  private final ConcurrentMap<Long, Boolean> collection = new ConcurrentHashMap<>();

  @Override
  public Collection<Long> get() {
    return collection.keySet();
  }

  @Override
  public boolean contains(Long n) {
    return collection.containsKey(n);
  }

  @Override
  public void collect(Long n) {
    collection.putIfAbsent(n, true);
  }

  @Override
  public Long last() {
    return -1L;
  }

}
