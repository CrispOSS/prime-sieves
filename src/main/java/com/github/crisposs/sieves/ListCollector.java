package com.github.crisposs.sieves;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

public class ListCollector implements Collector {

  private NavigableSet<Long> collection = new ConcurrentSkipListSet<>();
  private final AtomicLong last = new AtomicLong();

  @Override
  public void collect(Long n) {
    collection.add(n);
    last.getAndSet(collection.last());
  }

  @Override
  public Collection<Long> get() {
    return collection;
  }

  @Override
  public Long last() {
    return last.get();
  }

}
