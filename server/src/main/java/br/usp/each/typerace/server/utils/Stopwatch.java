package br.usp.each.typerace.server.utils;

public class Stopwatch {
  private long startTime = 0;
  private long stopTime = 0;
  private boolean running = false;

  public void start() {
    startTime = System.currentTimeMillis();
    running = true;
  }

  public void stop() {
    stopTime = System.currentTimeMillis();
    running = false;
  }

  public long getElapsedTime() {
    long elapsed;
    if (running) {
      elapsed = (System.currentTimeMillis() - startTime);
    } else {
      elapsed = (stopTime - startTime);
    }
    return elapsed;
  }

  public long getElapsedTimeSecs() {
    long elapsed;
    if (running) {
      elapsed = ((System.currentTimeMillis() - startTime) / 1000);
    } else {
      elapsed = ((stopTime - startTime) / 1000);
    }
    return elapsed;
  }
 
  public void clear() {
    startTime = 0;
    stopTime = 0;
    running = false;
  }
}
