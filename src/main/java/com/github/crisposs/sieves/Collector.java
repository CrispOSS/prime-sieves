package com.github.crisposs.sieves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface Collector extends Supplier<Collection<Long>> {

  void collect(Long n);

  default boolean contains(Long n) {
    return get().contains(n);
  }

  default Long last() {
    Collection<Long> s = get();
    if (s.isEmpty()) {
      return null;
    }
    if (s instanceof List) {
      return ((List<Long>) s).get(s.size() - 1);
    }
    List<Long> list = new ArrayList<>(s);
    return list.get(list.size() - 1);
  }

}
