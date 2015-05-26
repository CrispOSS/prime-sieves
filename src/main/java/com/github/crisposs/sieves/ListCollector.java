package com.github.crisposs.sieves;

import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class ListCollector implements Collector {

  private static final long serialVersionUID = 1L;

  private NavigableSet<Long> collection = new ConcurrentSkipListSet<>();
  private final AtomicLong last = new AtomicLong();

  @Override
  public void collect(Long n) {
    if (collection.add(n)) {
      last.getAndSet(collection.last());
    }
  }

  @Override
  public Stream<Long> get() {
    return collection.stream().sorted();
  }

  @Override
  public Long last() {
    return last.get();
  }

}
