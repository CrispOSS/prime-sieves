package com.github.crisposs.network.benchmark;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import abs.api.SystemContext;

public class CustomExecutor extends ThreadPoolExecutor {

  public static final Runnable INTERRUPT_HANDLER = new Runnable() {
    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    @Override
    public void run() {
      try {
        if (interrupted.compareAndSet(false, true)) {
          SystemContext.context().stop();
        }
      } catch (Exception e) {
        System.err.println(e.getMessage());
      }
    }
  };

  public static class CustomThreadFactory implements ThreadFactory {

    private final AtomicLong counter = new AtomicLong(0);
    private final String prefix;

    public CustomThreadFactory(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
      CustomThread thread =
          new CustomThread(r, prefix + "-jmh-custom-worker-" + counter.incrementAndGet());
      return thread;
    }

  }

  public static class CustomThread extends Thread {

    public CustomThread(Runnable target, String name) {
      super(target, name);
    }

    @Override
    public void interrupt() {
      INTERRUPT_HANDLER.run();
      super.interrupt();
    }
  }

  public CustomExecutor(int maxThreads, String prefix) {
    super(0, maxThreads, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
        new CustomThreadFactory(prefix));
  }

}
