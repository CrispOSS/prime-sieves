package com.github.crisposs.sieves;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import abs.api.Configuration;
import abs.api.Context;

public class Main {

  private final ListCollector collector = new ListCollector();

  public Main(Long N, Duration duration) {
    Context context = Configuration.newConfiguration().buildContext();
    Range range = new Range(1L, N, N);
    SieveMaster master = new SieveMaster(range, collector);
    context.newActor("master", master);
    Callable<Boolean> msg = () -> {
      master.sieve();
      return true;
    };
    Future<Boolean> result = context.await(master, msg);
    try {
      assert result.get();
      Long lastPrime = master.lastPrime();
      System.out.println(
          "Took " + context.upTime() + ": " + lastPrime + " / count = " + collector.get().size());
      context.stop();
    } catch (Exception e) {
    }
  }

  public Collection<Long> primes() {
    return collector.get();
  }

  public static void main(String[] args) {
    Long N = 50_000_000L;
    if (args.length > 0) {
      N = Long.parseLong(args[0]);
    }
    try {
      new Main(N, Duration.ofHours(1));
    } catch (Exception e) {
    }
  }

}
