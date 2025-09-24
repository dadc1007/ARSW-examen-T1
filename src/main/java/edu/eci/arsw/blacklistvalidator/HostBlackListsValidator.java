/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author hcadavid
 */
public class HostBlackListsValidator {

  public static final int BLACK_LIST_ALARM_COUNT = 5;

  /**
   * Check the given host's IP address in all the available black lists, and report it as NOT
   * Trustworthy when such IP was reported in at least BLACK_LIST_ALARM_COUNT lists, or as
   * Trustworthy in any other case. The search is not exhaustive: When the number of occurrences is
   * equal to BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as NOT Trustworthy,
   * and the list of the five blacklists returned.
   *
   * @param ipaddress suspicious host's IP address.
   * @return Blacklists numbers where the given host's IP address was found.
   */
  public List<Integer> checkHost(String ipaddress, int N) {
    List<HostBlackListsValidatorThread> threads = new ArrayList<>();
    HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
    Controller controller = new Controller();

    int fragment = skds.getRegisteredServersCount() / N;
    int residue = skds.getRegisteredServersCount() % N;
    int start = 0;
    int end = 0;

    for (int i = 0; i < N; i++) {
      end += (i == N - 1) ? fragment + residue : fragment;
      HostBlackListsValidatorThread thread =
          new HostBlackListsValidatorThread(skds, ipaddress, start, end, controller);
      thread.start();
      threads.add(thread);

      start = end;
    }

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    LinkedList<Integer> blackListOcurrences = new LinkedList<>();

    for (HostBlackListsValidatorThread thread : threads) {
      blackListOcurrences.addAll(thread.getBlackListOcurrences());
    }

    if (controller.getTotalOcurrencesCount() >= BLACK_LIST_ALARM_COUNT) {
      skds.reportAsNotTrustworthy(ipaddress);
    } else {
      skds.reportAsTrustworthy(ipaddress);
    }

    LOG.log(
        Level.INFO,
        "Checked Black Lists:{0} of {1}",
        new Object[] {controller.getTotalCheckedListsCount(), skds.getRegisteredServersCount()});

    return blackListOcurrences;
  }

  private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
}
