package com.github.crisposs.sieves.benchmark;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import com.github.crisposs.sieves.Main;

@BenchmarkMode(Mode.Throughput)
@Fork(value = 1)
@Warmup(iterations = 1, time = 32, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 16, time = 1, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(value = 128)
@State(Scope.Benchmark)
public class SieveBenchmark {

  @Param({"10000", "50000", "1000000"})
  public long N;

  @Benchmark
  public Collection<Long> sieve() throws Exception {
    Main main = new Main(N);
    return main.primes();
  }

}
