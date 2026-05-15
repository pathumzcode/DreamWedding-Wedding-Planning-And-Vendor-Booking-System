package com.wedding.dreamwedding.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FileService demonstrates Java File Handling concepts.
 * This is used to store system logs and export reports to physical files.
 */
@Service
public class FileService {

    private final String LOG_FILE_PATH = "system_logs.txt";

    /**
     * FILE HANDLING: WRITING TO A FILE
     * Demonstrates using FileWriter and BufferedWriter.
     * The 'true' parameter in FileWriter enables 'append' mode.
     */
    public void logActivity(String user, String action) {
        // Try-with-resources ensures the file is closed automatically
        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            String logEntry = String.format("[%s] USER: %s | ACTION: %s", 
                    LocalDateTime.now(), user, action);
            
            out.println(logEntry);
            System.out.println("FILE LOG: Activity recorded in " + LOG_FILE_PATH);
            
        } catch (IOException e) {
            System.err.println("FILE ERROR: Failed to write to " + LOG_FILE_PATH + " -> " + e.getMessage());
        }
    }

    /**
     * FILE HANDLING: READING FROM A FILE
     * Demonstrates using FileReader and BufferedReader to retrieve stored logs.
     */
    public List<String> readRecentLogs(int count) {
        List<String> logs = new ArrayList<>();
        File file = new File(LOG_FILE_PATH);
        
        if (!file.exists()) return logs;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
                if (logs.size() > count) logs.remove(0); // Keep only recent entries
            }
        } catch (IOException e) {
            System.err.println("FILE ERROR: Failed to read from " + LOG_FILE_PATH);
        }
        return logs;
    }

    /**
     * FILE HANDLING: EXPORTING DATA
     * Demonstrates creating a new file for a specific report.
     */
    public String exportBookingReport(String bookingId, String reportData) {
        String fileName = "booking_report_" + bookingId + ".txt";
        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            writer.println("--- DREAM WEDDING BOOKING REPORT ---");
            writer.println("Generated on: " + LocalDateTime.now());
            writer.println("------------------------------------");
            writer.println(reportData);
            writer.println("------------------------------------");
            writer.println("Thank you for using DreamWedding System!");
            return fileName;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
