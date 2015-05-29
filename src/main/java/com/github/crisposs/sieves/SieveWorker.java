package com.github.crisposs.sieves;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class SieveWorker implements Sieve {

  private static final long serialVersionUID = 1L;

  private final URI name;
  private final Collector collector;
  private final Range range;


  public SieveWorker(Range range, Collector collector) {
    this.range = range;
    this.name = URI.create(NS + range);
    this.collector = collector;
  }

  @Override
  public void sieve() {
    final Long last = last();
    final List<Long> primes = collector.get().collect(Collectors.toList());
    Runnable msg = () -> {
      Sieve s = sender();
      s.done(primes);
    };
    if (last != null && last != 0 && range.from() <= last) {
      reply(msg);
      return;
    }
    primes.addAll(sieveRange(range.from(), range.to(), primes, range.maxSqrt()));
    reply(msg);
  }

  @Override
  public Long last() {
    return collector.last();
  }

  @Override
  public void done(List<Long> n) {}

  @Override
  public String toString() {
    return name().toString();
  }

  @Override
  public URI name() {
    return name;
  }

  private List<Long> sieveRange(long from, long to, List<Long> primes, long maxSqrt) {
    if (primes.isEmpty()) {
      return findPrimes(to);
    }
    final List<Long> myPrimes = new ArrayList<>();
    final boolean fromEven = from % 2 == 0;
    final boolean toEven = to % 2 == 0;
    final long start = fromEven ? from + 1 : from;
    final long end = toEven ? to + 1 : to;
    final List<Long> numbers = oddNumbers(start, end);
    for (final long l : numbers) {
      if (isPrime(primes, l)) {
        myPrimes.add(l);
      }
    }
    return myPrimes;
  }

  private boolean isPrime(final List<Long> primes, long n) {
    for (Long p : primes) {
      if (n % p == 0) {
        return false;
      }
    }
    return true;
  }

  private List<Long> findPrimes(Long n) {
    final Long N = longSqrt(n);
    List<Long> numbers = oddNumbers(2, N);
    Set<Long> nonPrimes = new HashSet<>();
    for (Long l : numbers) {
      if (nonPrimes.contains(l)) {
        continue;
      }
      for (long m = l * l; m <= n; m = m + l) {
        nonPrimes.add(m);
      }
    }
    numbers.removeAll(nonPrimes);
    return numbers;
  }

  private List<Long> oddNumbers(final long start, final long end) {
    return LongStream.rangeClosed(start, end).filter(x -> x % 2 != 0).sorted().boxed()
        .collect(Collectors.toList());
  }

  private long longSqrt(Long n) {
    return new Long(Double.valueOf(Math.sqrt(n.doubleValue())).longValue());
  }

}
