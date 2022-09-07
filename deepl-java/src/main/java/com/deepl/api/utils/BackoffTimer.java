// Copyright 2022 DeepL SE (https://www.deepl.com)
// Use of this source code is governed by an MIT
// license that can be found in the LICENSE file.
package com.deepl.api.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class BackoffTimer {

  private int numRetries;
  private Duration backoff;
  private final Duration minTimeout;
  private Instant deadline;

  private static final Duration backoffInitial = Duration.ofSeconds(1);
  private static final Duration backoffMax = Duration.ofSeconds(120);
  private static final float jitter = 0.23F;
  private static final float multiplier = 1.6F;

  public BackoffTimer(Duration minTimeout) {
    numRetries = 0;
    backoff = backoffInitial;
    this.minTimeout = minTimeout;
    deadline = Instant.now().plus(backoff);
  }

  public Duration getTimeout() {
    Duration timeToDeadline = getTimeUntilDeadline();
    if (timeToDeadline.compareTo(minTimeout) < 0) return minTimeout;
    return timeToDeadline;
  }

  public long getTimeoutMillis() {
    return getTimeout().toMillis();
  }

  public int getNumRetries() {
    return numRetries;
  }

  public void sleepUntilRetry() throws InterruptedException {
    try {
      Thread.sleep(getTimeUntilDeadline().toMillis());
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw exception;
    }

    backoff = Duration.ofNanos((long) (backoff.toNanos() * multiplier));
    if (backoff.compareTo(backoffMax) > 0) backoff = backoffMax;

    float randomJitter = (ThreadLocalRandom.current().nextFloat() * 2.0F - 1.0F) * jitter + 1.0F;
    Duration jitteredBackoff = Duration.ofNanos((long) (backoff.toNanos() * randomJitter));
    deadline = Instant.now().plus(jitteredBackoff);
    ++numRetries;
  }

  private Duration getTimeUntilDeadline() {
    Instant currentTime = Instant.now();
    if (currentTime.isAfter(deadline)) return Duration.ZERO;
    return Duration.between(currentTime, deadline);
  }
}
