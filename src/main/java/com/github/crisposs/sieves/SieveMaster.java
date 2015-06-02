package com.github.crisposs.sieves;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class SieveMaster implements Sieve {

  private static final long serialVersionUID = 1L;

  private final CountDownLatch latch = new CountDownLatch(1);
  private final URI name = URI.create(NS + "master");
  private final Collector collector;
  private final Range range;
  private final List<Range> ranges;
  private final Queue<SieveWorker> workers = new ConcurrentLinkedQueue<>();

  public SieveMaster(Range range, Collector collector) {
    this.range = range;
    this.collector = collector;
    this.ranges = breakRange(range);
    for (Range r : ranges) {
      SieveWorker w = new SieveWorker(r, collector);
      workers.offer(w);
      context().newActor(w.toString(), w);
    }
  }

  @Override
  public void sieve() {
    SieveWorker w = workers.poll();
    if (w == null) {
      latch.countDown();
      return;
    }
    Runnable message = () -> {
      w.sieve();
    };
    send(w, message);
  }

  @Override
  public void done(List<Long> nums) {
    nums.forEach(n -> collector.collect(n));
    // System.out.println(collector.last());
    sieve();
  }

  @Override
  public Long last() {
    return collector.last();
  }

  @Override
  public String toString() {
    return name().toString();
  }

  @Override
  public URI name() {
    return name;
  }

  public Long lastPrime() {
    try {
      latch.await();
      return last();
    } catch (InterruptedException e) {
      return null;
    }
  }

  private List<Range> breakRange(Range range) {
    final Double order = Double.valueOf(Math.log10(range.to().doubleValue()));
    final int size = range.to().intValue() / (order.intValue() + 1);
    // System.out.println("bucket size: " + size);
    if (range.to() <= size) {
      return Collections.singletonList(range);
    }
    List<Range> ranges = new ArrayList<>();
    long from = range.from();
    do {
      long to = (from + size - 1 > range.to()) ? range.to() : from + size - 1;
      Range r = new Range(from, to, range.max());
      ranges.add(r);
      from = r.to() + 1;
    } while (from < range.to());
//    System.out.println("ranges: " + ranges.size());
    return ranges;
  }

}
