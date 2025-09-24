package edu.eci.arsw.blacklistvalidator;

public class Controller {
  private int totalOcurrencesCount;
  private int totalCheckedListsCount;
  private boolean finished;
  private final Object lock;

  public Controller() {
    this.totalOcurrencesCount = 0;
    this.finished = false;
    this.lock = new Object();
  }

  public void finish() {
    synchronized (lock) {
      if (!isFinished()) {
        if (totalOcurrencesCount == HostBlackListsValidator.BLACK_LIST_ALARM_COUNT) {
          finished = true;
        }
      }
    }
  }

  public void incrementTotalOcurrencesCount() {
    synchronized (lock) {
      this.totalOcurrencesCount++;
    }
  }

  public void incrementTotalCheckedListsCount() {
    synchronized (lock) {
      this.totalCheckedListsCount++;
    }
  }

  public int getTotalOcurrencesCount() {
    synchronized (lock) {
      return this.totalOcurrencesCount;
    }
  }

  public int getTotalCheckedListsCount() {
    return this.totalCheckedListsCount;
  }

  public boolean isFinished() {
    synchronized (lock) {
      return this.finished;
    }
  }

  public Object getLock() {
    return this.lock;
  }
}
