/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.undra.abba.bruteforce;

import br.edu.undra.abba.analyzer.Analyzer;
import br.edu.undra.abba.analyzer.Bounds;
import br.edu.undra.abba.runner.AnalyzerRunnable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexandre
 */
public class Forker {

    private static long packetSize;
    private static long packetFullSize;
    private static boolean runThreadsAtCreation = false;

    private static long packetsNumber;
    private static long remainder;
    public static long upPacketsNumber = 0;
    public static long bottomPacketsNumber = 0;

    private static boolean alreadyAjusted = false;
    private static long to = 0;
    private static long from = 0;

    private static String initial;
    private static String target;

    static private boolean verbose = false;

    static public void runThreadsAtCreation() {
        runThreadsAtCreation = true;
    }

    static public void runThreadsAfterCreation() {
        runThreadsAtCreation = false;
    }

    static public void verboseOff() {
        System.out.println("turning verbose off");
        verbose = false;
    }

    static public void verboseOn() {
        System.out.println("turning verbose on");
        verbose = true;
    }

    static public boolean isVerboseOn() {
        return verbose == true;
    }

    public static List<Packet> packets = new ArrayList<>();

    static void setPacketSize(long packetSize) {
        Forker.packetSize = packetSize;
    }

    static void setPacketFullSize(long packetFullSize) {
        Forker.packetFullSize = packetFullSize;
    }

    static void fork(Class lock, String initial, String target) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        long init = System.nanoTime();

        setUp();

        long totalOfCombinations = packetFullSize;

        synchronized (Analyzer.lock) {
            //forks
            while (packetsNumber > 0) {

                to = totalOfCombinations - 1;
                from = to - packetSize;

                if (from >= packetFullSize / 2) {
                    setAndCreateUpperPacket(lock, initial, target);
                } else if (!alreadyAjusted) {
                    setAndCreatePacket(lock, initial, target);
                } else {
                    setAndCreateBottomPacket(lock, initial, target);
                }

                totalOfCombinations = from;

                packetsNumber--;

            }

            if (remainder > 0) {
                setAndCreateRemainderPackets(lock, initial, target);
            }

            if (verbose) {
                showPacketsStatistics();
            }

            if (!runThreadsAtCreation) {//runs after creation
                for (Packet p : packets) {
                    p.thread.start();
                }
            }

        }

    }

    private static void setUp() {

        try {
            
            packets.clear();
            packetsNumber = (packetFullSize) / packetSize;
            remainder = (packetFullSize) % packetSize;
            upPacketsNumber = 0;
            bottomPacketsNumber = 0;

            alreadyAjusted = false;
            to = 0;
            from = 0;

            if (verbose) {
                System.out.println("\tSplitting a " + packetFullSize + " full size packet into " + "[#" + packetsNumber + "]" + " smaller packets. Each smaller packet has size of " + (packetsNumber==0?packetsNumber:packetSize));
                System.out.println("\tRuntime available cores # " + Runtime.getRuntime().availableProcessors());

            }
            
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong :(   " + e.getMessage(), e.getCause());
        }

    }

    private static void showPacketsStatistics() {
        System.out.println("\tPackets        " + "[#" + packets.size() + "]");
        System.out.println("\tUp Packets     " + "[#" + upPacketsNumber + "]");
        System.out.println("\tBottom Packets " + "[#" + bottomPacketsNumber + "]");
    }

    private static void setAndCreateRemainderPackets(Class lock, String initial1, String target1) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalArgumentException, SecurityException, IllegalAccessException {
        Packet p = new Packet(0, (from - 1)<0?packetFullSize:from);
        p.thread = new Thread(new AnalyzerRunnable((Analyzer) lock.getConstructor(String.class, String.class).newInstance(new String(initial1), new String(target1)), 0, (from - 1)<0?packetFullSize:from, Bounds.UPPER));
        if (runThreadsAtCreation) {
            p.thread.start();
        }
        packets.add(p);
        ++upPacketsNumber;
        p = new Packet(0, (from - 1)<0?packetFullSize:from);
        p.thread = new Thread(new AnalyzerRunnable((Analyzer) lock.getConstructor(String.class, String.class).newInstance(new String(initial1), new String(target1)), 0, (from - 1)<0?packetFullSize:from, Bounds.BOTTOM));
        if (runThreadsAtCreation) {
            p.thread.start();
        }
        packets.add(p);
        ++bottomPacketsNumber;
    }

    private static void setAndCreateBottomPacket(Class lock, String initial1, String target1) throws IllegalAccessException, InstantiationException, IllegalArgumentException, SecurityException, NoSuchMethodException, InvocationTargetException {
        Packet p = new Packet(from, to);
        p.thread = new Thread(new AnalyzerRunnable((Analyzer) lock.getConstructor(String.class, String.class).newInstance(new String(initial1), new String(target1)), from, to, Bounds.BOTTOM));
        if (runThreadsAtCreation) {
            p.thread.start();
        }
        packets.add(p);
        bottomPacketsNumber = packets.size() - upPacketsNumber;
    }

    private static void setAndCreatePacket(Class lock, String initial1, String target1) throws IllegalArgumentException, NoSuchMethodException, SecurityException, InstantiationException, InvocationTargetException, IllegalAccessException {
        Packet p = new Packet(packetFullSize / 2, to);
        p.thread = new Thread(new AnalyzerRunnable((Analyzer) lock.getConstructor(String.class, String.class).newInstance(new String(initial1), new String(target1)), packetFullSize / 2, to, Bounds.UPPER));
        if (runThreadsAtCreation) {
            p.thread.start();
        }
        packets.add(p);
        alreadyAjusted = true;
        from = packetFullSize / 2;
        upPacketsNumber = packets.size();
    }

    private static void setAndCreateUpperPacket(Class lock, String initial1, String target1) throws IllegalAccessException, SecurityException, InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalArgumentException {
        Packet p = new Packet(from, to);
        p.thread = new Thread(new AnalyzerRunnable((Analyzer) lock.getConstructor(String.class, String.class).newInstance(new String(initial1), new String(target1)), from, to, Bounds.UPPER));
        if (runThreadsAtCreation) {
            p.thread.start();
        }
        packets.add(p);
        upPacketsNumber = packets.size();
    }

}
