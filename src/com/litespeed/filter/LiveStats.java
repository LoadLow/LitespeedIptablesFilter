package com.litespeed.filter;

import com.litespeed.stats.ConnectionsStats;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author LoadLow
 */
public class LiveStats {

    public final CopyOnWriteArrayList<String> bannedAddresses;
    public final Logger floodLog;
    public final Logger attackLog;
    public final Logger errorsLog;
    public final Logger statsLog;
    public volatile ConnectionsStats lastReport = null;
    public volatile long lastVerif = 0;
    public volatile boolean SecurityEnabled = false;
    public volatile long lastStatsLog = 0;

    public LiveStats() {
        bannedAddresses = new CopyOnWriteArrayList<String>();
        floodLog = new Logger("flood.log");
        attackLog = new Logger("attack.log");
        errorsLog = new Logger("errors.log");
        statsLog = new Logger("stats.log");
    }

    public void logStats(String stats) {
        if ((System.currentTimeMillis() - lastStatsLog) > Kernel.Config.INTERVAL_LOG_STATS) {
            statsLog.log(stats);
            lastStatsLog = System.currentTimeMillis();
        }
    }
}
