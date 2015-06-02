package com.github.crisposs.sieves.benchmark;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import com.github.crisposs.sieves.Main;

@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1)
@Warmup(iterations = 1, time = 32, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 4, time = 1, timeUnit = TimeUnit.SECONDS)
@Timeout(time = SieveBenchmark.TIMEOUT, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SieveBenchmark {

  public static final int TIMEOUT = 2;
  public static final Duration DURATION = Duration.ofSeconds(TIMEOUT);

  @Param({"500000", "1000000", "2000000", "4000000", "8000000"})
  public long N;

  @Benchmark
  public Collection<Long> sieve() throws Exception {
    Main main = new Main(N, DURATION);
    return main.primes();
  }

}
