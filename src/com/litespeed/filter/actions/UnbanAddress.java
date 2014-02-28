package com.litespeed.filter.actions;

import com.litespeed.filter.Kernel;
import java.io.IOException;
import java.util.TimerTask;

/**
 *
 * @author LoadLow
 */
public class UnbanAddress extends TimerTask {

    private String address;

    public UnbanAddress(String address) {
        this.address = address;
    }

    @Override
    public synchronized void run() {
        try {
            if (Kernel.LiveStats.bannedAddresses.contains(address)) {
                 Runtime.getRuntime().exec("iptables -D INPUT -p tcp --dport 80 -s "+address+" -j DROP");
                 Kernel.LiveStats.bannedAddresses.remove(address);
                 Kernel.LiveStats.floodLog.log(address + ", UNBAN");
            }
        } catch (IOException ex) {
            Kernel.LiveStats.errorsLog.log("Error unban : "+ address);
        } finally {
            this.cancel();
        }
    }

    @Override
    public boolean cancel() {
        try {
            return super.cancel();
        } finally {
            address = null;
        }
    }
}
