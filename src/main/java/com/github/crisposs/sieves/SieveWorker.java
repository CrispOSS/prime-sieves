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
  private final Collector primeCollector;
  private final Collector nonPrimeCollector;
  private final Range range;


  public SieveWorker(Range range, Collector primeCollector, Collector nonPrimeCollector) {
    this.range = range;
    this.nonPrimeCollector = nonPrimeCollector;
    this.name = URI.create(NS + range);
    this.primeCollector = primeCollector;
  }

  @Override
  public void sieve() {
    final Long last = last();
    final List<Long> primes = new ArrayList<>(primeCollector.get());
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
    return primeCollector.last();
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
    Set<Long> myPrimes = new HashSet<>();
    long l = from % 2 == 0 ? from + 1 : from;
    while (l <= to) {
      if (nonPrimeCollector.contains(l)) {
        l += 2;
        continue;
      }
      boolean isPrime = true;
      for (long p : primes) {
        if (isPrime) {
          long mod = l % p;
          if (mod == 0) {
            nonPrimeCollector.collect(l);
            isPrime = false;
          }
        }
        if (p >= maxSqrt) {
          break;
        }
        long m = p * l;
        if (m <= maxSqrt) {
          nonPrimeCollector.collect(m);
        }
      }
      if (isPrime) {
        myPrimes.add(l);
      }
      l += 2;
    }
    return new ArrayList<>(myPrimes);
  }

  private List<Long> findPrimes(Long n) {
    final Long N = longSqrt(n);
    List<Long> numbers = oddNumbers(2, N).boxed().collect(Collectors.toList());
    for (Long l : numbers) {
      if (nonPrimeCollector.contains(l)) {
        continue;
      }
      for (long m = l * l; m <= n; m = m + l) {
        nonPrimeCollector.collect(m);
      }
    }
    numbers.removeAll(nonPrimeCollector.get());
    return numbers;
  }

  private LongStream oddNumbers(final long start, final long end) {
    return LongStream.rangeClosed(start, end).sorted().filter(x -> x % 2 == 1);
  }

  private long longSqrt(Long n) {
    return new Long(Double.valueOf(Math.sqrt(n.doubleValue())).longValue());
  }

}
