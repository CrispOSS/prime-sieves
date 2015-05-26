package com.github.crisposs.sieves;

import java.util.List;

import abs.api.Actor;

public interface Sieve extends Actor {

  void sieve();

  Long last();

  void done(List<Long> n);

}
