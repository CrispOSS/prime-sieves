package com.github.crisposs.sieves;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
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
    final List<Long> primes = new ArrayList<>(collector.get());
    Sieve s = sender();
    Runnable msg = () -> {
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
    Set<Long> nonPrimes = new ConcurrentSkipListSet<>();
    oddNumbers(from, to).parallel().forEach(l -> {
      if (!nonPrimes.contains(l)) {
        for (long p : primes) {
          if (l % p == 0) {
            nonPrimes.add(l);
          }
          long m = p * l;
          if (m <= to) {
            nonPrimes.add(m);
          } else {
            break;
          }
        }
      }
    });
    List<Long> myPrimes = oddNumbers(from, to).boxed().collect(Collectors.toList());
    myPrimes.removeAll(nonPrimes);
    return myPrimes;
  }

  private List<Long> findPrimes(Long n) {
    final Long N = longSqrt(n);
    List<Long> numbers = oddNumbers(2, N).boxed().collect(Collectors.toList());
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

  private LongStream oddNumbers(final long start, final long end) {
    return LongStream.rangeClosed(start, end).filter(x -> x % 2 != 0);
  }

  private long longSqrt(Long n) {
    return new Long(Double.valueOf(Math.sqrt(n.doubleValue())).longValue());
  }

}
