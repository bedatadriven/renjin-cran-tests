package org.renjin.cran;

public enum BuildOutcome {
  /**
   * The Build failed due to an error, such as OutOfMemoryException
   * or other potentially transient issue
   */
  ERROR,
  /**
   * The build failed
   */
  FAILED,
  /**
   * The build succeeded: an artifact
   * was produced
   */
  SUCCESS,

  /**
   * The build timed out before completing
   */
  TIMEOUT,

  /**
   * The package was not built at all due to a missing dependency
   */
  NOT_BUILT
}
