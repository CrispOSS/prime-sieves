package com.github.crisposs.sieves;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import abs.api.Actor;

public interface Collector extends Actor, Supplier<Stream<Long>> {

  void collect(Long n);

  default Long last() {
    Stream<Long> s = get();
    if (s.count() == 0) {
      return null;
    }
    List<Long> list = s.sorted().collect(Collectors.toList());
    return list.get(list.size() - 1);
  }

}
