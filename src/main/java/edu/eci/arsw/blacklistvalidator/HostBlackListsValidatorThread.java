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

  public HostBlackListsValidatorThread(
      HostBlacklistsDataSourceFacade skds, String ipaddress, int start, int end) {
    this.blackListOcurrences = new LinkedList<>();
    this.ocurrencesCount = 0;
    this.checkedListsCount = 0;
  }

  @Override
  public void run() {
    for (int i = start; i < end; i++) {
      checkedListsCount++;

      if (skds.isInBlackListServer(i, ipaddress)) {
        blackListOcurrences.add(i);
        ocurrencesCount++;
      }
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
