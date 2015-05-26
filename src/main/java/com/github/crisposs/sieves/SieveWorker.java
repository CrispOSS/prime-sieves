package com.github.crisposs.sieves;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
    primes.addAll(sieveRange(range.from(), range.to(), primes));
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

  private List<Long> sieveRange(long from, long to, List<Long> currentPrimes) {
    if (currentPrimes.isEmpty()) {
      return findPrimes(to);
    }
    final List<Long> myPrimes = new ArrayList<>();
    final boolean firstEven = from % 2 == 0;
    final long start = firstEven ? from + 1 : from;
    for (long n = start; n <= to; n = n + 2) {
      if (isPrime(currentPrimes, n)) {
        myPrimes.add(n);
      }
    }
    if (isPrime(currentPrimes, from)) {
      myPrimes.add(from);
    } else {
      myPrimes.remove(from);
    }
    if (isPrime(currentPrimes, to)) {
      myPrimes.add(to);
    } else {
      myPrimes.remove(to);
    }
    return myPrimes;
  }

  private boolean isPrime(final List<Long> currentPrimes, long n) {
    for (Long p : currentPrimes) {
      if (n % p == 0) {
        return false;
      }
    }
    return true;
  }

  private List<Long> findPrimes(long n) {
    List<Long> primes = new ArrayList<>(Arrays.asList(2L, 3L, 5L));
    primes.addAll(LongStream.range(6, n + 1).filter(p -> p % 2 != 0).filter(p -> p % 3 != 0)
        .filter(p -> p % 5 != 0).sorted().boxed().collect(Collectors.toList()));
    Set<Long> nonPrimes = new HashSet<>();
    for (Long p : primes) {
      for (long i = 2; i <= p; ++i) {
        if (primes.contains(i * p)) {
          nonPrimes.add(i * p);
        }
      }
    }
    primes.removeAll(nonPrimes);
    return primes;
  }


}
