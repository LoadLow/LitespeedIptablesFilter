package com.litespeed.filter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 *
 * @author LoadLow
 */
public class Config {

    public long TIME_ADDRESS_BANNED = 10 * 1000 * 60; // 10 minutes (= in litespeed)
    //
    public long INTERVAL_ANALYZE_LITESPEED = 2500; // 2,5 sec
    public long INTERVAL_FLUSH_LOGS = 5 * 1000 * 60; // 5 minutes
    public long INTERVAL_LOG_STATS = 15 * 1000 * 60; // 15 minutes
    public long INTERVAL_ANALYZE_REPORT = 1000 * 30; //30 secs
    //
    public String LITESPEED_REPORT_FOLDER = "";

    public Config(String filePath) throws ConfigurationException {
        XMLConfiguration xml = new XMLConfiguration();
        xml.setListDelimiter(';');
        xml.setFileName(filePath);
        xml.setAttributeSplittingDisabled(true);
        try {
            xml.load();
            parseXML(xml);
        } catch (ConfigurationException ex) {
            throw ex;
        }
    }

    private void parseXML(XMLConfiguration xml) {
        LITESPEED_REPORT_FOLDER = xml.getString("com.litespeed.filter.ls-report-folder", LITESPEED_REPORT_FOLDER);

        INTERVAL_ANALYZE_REPORT = xml.getLong("com.litespeed.filter.intervals.analyze-report", INTERVAL_ANALYZE_REPORT) * 1000;
        INTERVAL_FLUSH_LOGS = xml.getLong("com.litespeed.filter.intervals.flush-logs", INTERVAL_FLUSH_LOGS) * 1000;
        INTERVAL_ANALYZE_LITESPEED = xml.getLong("com.litespeed.filter.intervals.analyze-litespeed", INTERVAL_ANALYZE_LITESPEED);
        INTERVAL_LOG_STATS = xml.getLong("com.litespeed.filter.intervals.log-stats", INTERVAL_LOG_STATS) * 1000 * 60;


        TIME_ADDRESS_BANNED = xml.getLong("com.litespeed.filter.times.address-banned", TIME_ADDRESS_BANNED) * 1000 * 60;
    }
}