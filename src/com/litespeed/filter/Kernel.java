package com.litespeed.filter;

import com.litespeed.filter.actions.AnalyzeLitespeedStats;
import com.litespeed.stats.StatsFile;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author LoadLow
 */
public class Kernel {

    public static Processor Processor;
    public static Processor AnalyzeProcessor;
    public static AnalyzeLitespeedStats AnalyzeLitespeedStats;
    public static LiveStats LiveStats;
    public static Config Config;
    public static ArrayList<StatsFile> StatsFiles = new ArrayList<StatsFile>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                exit();
            }
        });
    }

    public synchronized static void exit() {
        System.out.println("Stopping Filter...");

        if (AnalyzeLitespeedStats != null) {
            System.out.println("... stopping Analyze Task");
            AnalyzeLitespeedStats.cancel();
            AnalyzeLitespeedStats = null;
        }

        if (AnalyzeProcessor != null) {
            System.out.println("... stopping Analyze Processor");
            AnalyzeProcessor.stop();
            AnalyzeProcessor = null;
        }

        if (Processor != null) {
            System.out.println("... stopping Processor");
            Processor.stop();
            Processor = null;
        }

        if (LiveStats != null) {
            System.out.println("... flushing/closing Logs");
            LiveStats.statsLog.close();
            LiveStats.floodLog.close();
            LiveStats.errorsLog.close();
            LiveStats.attackLog.close();
            LiveStats = null;
        }

        if (StatsFiles != null) {
            StatsFiles.clear();
            StatsFiles = null;
        }
        Config = null;


        System.out.println("Filter stopped!");
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("==========================================");
        System.out.println("LitespeedFilter using Iptables by LoadLow");
        System.out.println("==========================================");
        System.out.println("Filter starting...");
        System.out.println();

        System.out.print("Loading Config...");
        String configPath = "config.xml";
        if (args.length > 0) {
            configPath = args[0];
        }
        try {
            Config = new Config(configPath);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex1) {
            }
            System.exit(0);
            return;
        }
        System.out.println("Ok!");

        System.out.print("Checking LS-Stats report files...");
        File rtreportFolder = new File(Config.LITESPEED_REPORT_FOLDER);
        if (!rtreportFolder.exists()) {
            System.out.println("Error: LS-Stats report folder not found.");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex1) {
            }
            System.exit(0);
            return;
        } else {
            for (File file : rtreportFolder.listFiles()) {
                if (file.getName().startsWith(".rtreport")) {
                    StatsFiles.add(new StatsFile(file.getPath()));
                }
            }
        }
        if (StatsFiles.isEmpty()) {
            System.out.println("Error: no LS-Stats report file found.");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex1) {
            }
            System.exit(0);
            return;
        } else {
            System.out.println("Ok: " + StatsFiles.size() + " files found.");
        }

        System.out.println("Starting Processors...");
        Processor = new Processor();
        AnalyzeProcessor = new Processor();

        System.out.println("Creating Stats&Logs...");
        LiveStats = new LiveStats();

        System.out.println("Dispatching Analyzer...");
        AnalyzeLitespeedStats = new AnalyzeLitespeedStats();
        AnalyzeProcessor.executeLoop(AnalyzeLitespeedStats, Config.INTERVAL_ANALYZE_LITESPEED);

        System.out.println("Filter started!");
        System.out.println();
        System.out.println("Filtering...");
    }
}
