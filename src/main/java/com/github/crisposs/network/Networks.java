package com.github.crisposs.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import abs.api.Actor;
import abs.api.Configuration;
import abs.api.Context;
import abs.api.Response;

public class Networks {

  static class Network implements Actor {
    private static final long serialVersionUID = 1L;

    private final AtomicLong token = new AtomicLong(0);

    public Long newToken() {
      Long t = token.incrementAndGet();
      return t;
    }

    @Override
    public String simpleName() {
      return "network";
    }

    @Override
    public String toString() {
      return simpleName();
    }
  }

  static class Packet implements Actor {
    private static final long serialVersionUID = 1L;

    private final Network network;

    public Packet(Network network) {
      this.network = network;
    }

    public Long transmit() {
      Callable<Long> message = () -> network.newToken();
      Response<Long> future = await(network, message);
      Long token = future.getValue();
      return token;
    }

    @Override
    public String simpleName() {
      return "Packet@" + Integer.toHexString(hashCode());
    }

    @Override
    public String toString() {
      return simpleName();
    }
  }

  static class Gateway implements Actor {
    private static final long serialVersionUID = 1L;

    private final Network network;

    public Gateway(Network network) {
      this.network = network;
    }

    public List<Long> relay(final int size, final boolean await) {
      List<Response<Long>> result = new ArrayList<>();
      for (int i = 1; i <= size; ++i) {
        Packet packet = new Packet(network);
        context().newActor(packet.simpleName(), packet);
        Callable<Long> message = () -> packet.transmit();
        Response<Long> res = await ? await(packet, message) : send(packet, message);
        result.add(res);
      }
      List<Long> tokens = new ArrayList<>();
      for (int i = 0; i < result.size(); ++i) {
        Response<Long> r = result.get(i);
        Long t = r.getValue();
        tokens.add(t);
      }
      return tokens;
    }

    @Override
    public String simpleName() {
      return "gateway";
    }

    @Override
    public String toString() {
      return simpleName();
    }

  }

  public static List<Long> operate(final int size, final boolean await) throws Exception {
    Context context = Configuration.newConfiguration().buildContext();
    Network network = new Network();
    context.newActor("network", network);
    Gateway gateway = new Gateway(network);
    context.newActor("gateway", gateway);

    Callable<List<Long>> msg = () -> gateway.relay(size, await);
    Response<List<Long>> res = context.await(gateway, msg);
    List<Long> value = res.getValue();
    System.out.println("Relays: " + value.size());
    try {
      context.stop();
    } catch (Exception e) {
    }
    return value;
  }

  public static void main(String[] args) throws Exception {
    int size = 40000;
    boolean await = true;
    if (args.length == 1) {
      size = Integer.parseInt(args[0]);
    }
    if (args.length == 2) {
      await = Boolean.parseBoolean(args[1]);
    }
    Networks.operate(size, await);
  }

}
