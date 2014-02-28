package com.litespeed.filter.actions;

import com.litespeed.filter.Kernel;
import com.litespeed.stats.Stats;
import com.litespeed.stats.StatsFile;
import java.io.FileNotFoundException;
import java.util.TimerTask;

/**
 *
 * @author LoadLow
 */
public class AnalyzeLitespeedStats extends TimerTask {

    @Override
    public synchronized void run() {
        Stats stats = null;
        for (StatsFile listedFile : Kernel.StatsFiles) {
            try {
                if (stats == null) {
                    stats = listedFile.loadAll();
                } else {
                    stats.merge(listedFile.loadAll());
                }
            } catch (FileNotFoundException e) {
                //Le fichier n'a pas ete trouve, litespeed a du le remplacer
            } catch (Exception e) {
                Kernel.LiveStats.errorsLog.logErr(e);
            }
        }
        if (stats == null) {
            Kernel.LiveStats.errorsLog.log("No rtreport file found.");
            return;
        }
        try {
            String[] file_blockedList = stats.bannedIPs.array;
            for (String ip : file_blockedList) {
                if (!Kernel.LiveStats.bannedAddresses.contains(ip)) {
                   Runtime.getRuntime().exec("iptables -I INPUT -p tcp --dport 80 -s "+ip+" -j DROP");
                   Kernel.LiveStats.floodLog.log(ip + ", BAN, " + Kernel.Config.TIME_ADDRESS_BANNED + "ms");
                   Kernel.LiveStats.bannedAddresses.add(ip);
                   Kernel.Processor.executeAfter(new UnbanAddress(ip), (Kernel.Config.TIME_ADDRESS_BANNED + (15 * 1000)));
                }
            }
            Kernel.LiveStats.logStats(stats.toString());
        } catch (Exception e) {
            Kernel.LiveStats.errorsLog.logErr(e);
        }
    }
}
