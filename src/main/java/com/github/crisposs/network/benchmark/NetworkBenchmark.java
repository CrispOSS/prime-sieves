package com.github.crisposs.network.benchmark;

import java.util.Collection;
import java.util.Collections;
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

import com.github.crisposs.network.Networks;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 1, time = 32, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Timeout(time = 5, timeUnit = TimeUnit.SECONDS)
public class NetworkBenchmark {

  @Param({"10000"})
  public int size;

  @Param({"false", "true"})
  public boolean await;

  @Benchmark
  public Collection<Long> operate() {
    try {
      return Networks.operate(size, await);
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

}
