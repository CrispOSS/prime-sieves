package com.github.crisposs.sieves;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;

public class BitSetCollector implements Collector {

  private final BitSet bits = new BitSet();

  @Override
  public Collection<Long> get() {
    return Collections.emptyList();
  }

  @Override
  public void collect(Long n) {
    bits.set(n.intValue());
  }

  @Override
  public boolean contains(Long n) {
    return bits.get(n.intValue());
  }

  @Override
  public Long last() {
    return -1L;
  }

}
