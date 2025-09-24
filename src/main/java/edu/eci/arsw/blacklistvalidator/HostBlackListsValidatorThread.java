package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;

public class HostBlackListsValidatorThread extends Thread {
  LinkedList<Integer> blackListOcurrences;
  int ocurrencesCount;
  int checkedListsCount;
  HostBlacklistsDataSourceFacade skds;
  private String ipaddress;
  int start;
  int end;
  Controller controller;

  public HostBlackListsValidatorThread(
      HostBlacklistsDataSourceFacade skds,
      String ipaddress,
      int start,
      int end,
      Controller controller) {
    this.blackListOcurrences = new LinkedList<>();
    this.ocurrencesCount = 0;
    this.checkedListsCount = 0;
    this.skds = skds;
    this.ipaddress = ipaddress;
    this.start = start;
    this.end = end;
    this.controller = controller;
  }

  @Override
  public void run() {
    for (int i = start; i < end && !this.controller.isFinished(); i++) {
      checkedListsCount++;
      this.controller.incrementTotalCheckedListsCount();

      if (skds.isInBlackListServer(i, ipaddress)) {
        blackListOcurrences.add(i);
        ocurrencesCount++;
        this.controller.incrementTotalOcurrencesCount();
      }

      this.controller.finish();
    }
  }

  public LinkedList<Integer> getBlackListOcurrences() {
    return this.blackListOcurrences;
  }

  public int getOcurrencesCount() {
    return this.ocurrencesCount;
  }

  public int getCheckedListsCount() {
    return this.checkedListsCount;
  }
}
