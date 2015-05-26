package com.github.crisposs.sieves;

import java.io.Serializable;

public class Range implements Serializable, Comparable<Range> {

  private static final long serialVersionUID = 1L;

  public static Range of(Long from, Long to) {
    return new Range(from, to);
  }

  private final Long from;
  private final Long to;

  public Range(Long from, Long to) {
    if (from > to) {
      throw new IllegalArgumentException("Range is not valid: [" + from + "," + to + "]");
    }
    this.from = from;
    this.to = to;
  }

  public Long from() {
    return from;
  }

  public Long to() {
    return to;
  }

  public Long length() {
    return to - from + 1;
  }

  public boolean contains(Long n) {
    return from <= n && n <= to;
  }

  @Override
  public String toString() {
    return "(" + from + "," + to + ")";
  }

  @Override
  public int compareTo(Range o) {
    int fc = from.compareTo(o.from);
    int tc = to.compareTo(o.to);
    if (fc - tc == 0) {
      return 0;
    }
    if (fc == 0) {
      return tc;
    }
    if (tc == 0) {
      return fc;
    }
    if (to < o.from) {
      return -1;
    }
    if (from > o.to) {
      return 1;
    }
    if (from < o.from && to > o.to) {
      return 1;
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj instanceof Range == false) {
      return false;
    }
    return compareTo((Range) obj) == 0;
  }

}
