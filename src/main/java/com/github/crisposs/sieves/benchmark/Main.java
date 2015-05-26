package com.github.crisposs.sieves.benchmark;

import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.profile.LinuxPerfProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

public class Main {

  public static void main(String[] args) throws RunnerException {
    Options options = new OptionsBuilder().include(SieveBenchmark.class.getSimpleName())
        .result("/tmp/abs-api-sieves.csv").resultFormat(ResultFormatType.CSV).shouldDoGC(true)
//        .addProfiler(LinuxPerfProfiler.class).addProfiler(LinuxPerfNormProfiler.class)
//        .addProfiler(GCProfiler.class)
        .detectJvmArgs().verbosity(VerboseMode.EXTRA).build();
    new Runner(options).run();
  }

}
