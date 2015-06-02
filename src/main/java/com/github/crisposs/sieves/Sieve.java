package com.github.crisposs.sieves;

import java.util.List;

import abs.api.Actor;

public interface Sieve extends Actor {

  void sieve();

  void done(List<Long> primes);

  Long last();
}
