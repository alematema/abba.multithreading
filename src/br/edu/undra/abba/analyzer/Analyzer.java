/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.undra.abba.analyzer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author alexandre
 */
public class Analyzer {

    static {

        binaryKey = new char[0];
        theFounderThread = null;
        lock = "lock";
        verbose = false;
        zero = new AtomicLong(0);
        totalOfThreadsRunning = new AtomicLong(0);

    }

    public static Timer timer;

    private final String initial;//the initial string
    private final String target;//the target string
    private final StringBuilder initialSB;//the initial string as a builder

    static volatile public char[] binaryKey;
    static volatile public boolean verbose;
    static private volatile Thread theFounderThread;
    static private final AtomicLong zero;
    static public volatile AtomicLong totalOfThreadsRunning;//has principal memory synchronization through volatile key word, AND neeed atomicity
    static public final String lock;

    public Analyzer(String initials, String targets) {
        initial = initials;
        target = targets;
        initialSB = new StringBuilder(initial);
        incrementThreadCount();
    }

    public void analyse(long from, long to, Bounds bound) {

        if (verbose) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> " + bound + " " + Thread.currentThread().getName() + " analysing from " + from + " to " + to + " [" + Thread.currentThread().getState() + "]");
        }

        long init = System.currentTimeMillis();

        Calendar startTime = Calendar.getInstance();

        for (long key = to; key >= from; key--) {

            if (secretIsClosed()) {

                if (keyOpensSecret(key, bound, init, startTime)) {
                    break;
                }

            } else {
                break;
            }
        }

        synchronized (Analyzer.lock) {
            update(init, startTime, from, to, bound);
        }

    }

    private boolean keyOpensSecret(long key, Bounds bound, long init, Calendar startTime) {
        char[] binaryKey = Long.toBinaryString(key).toCharArray();
        if (bound.equals(Bounds.BOTTOM)) {
            binaryKey = completeLeftZeroes(binaryKey, target.length() - initial.length());
        }
        if (binaryKeyOpensSecret(binaryKey)) {
            synchronized (this) {
                logKeyOpensSecret(init, startTime, bound, binaryKey);
                notifyOtherThreadsTheyShouldStopSearching(binaryKey);
                return true;
            }
        }
        return false;
    }

    public static boolean secretIsClosed() {
        return binaryKey.length == 0;//has principal memory synchronization through volatile key word
    }

    private void notifyOtherThreadsTheyShouldStopSearching(char[] binaryKey) {
        this.binaryKey = binaryKey;//has principal memory synchronization through volatile key word
        theFounderThread = Thread.currentThread();
    }

    private void logKeyOpensSecret(long init, Calendar startTime, Bounds bound, char[] binaryKey) {
        long took;
        took = System.currentTimeMillis() - init;
        if (verbose) {
            System.out.println(String.format("[%tT]", startTime) + bound + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + Thread.currentThread().getName() + " FOUND KEY " + new String(binaryKey) + "[took " + took / 1000 + " s]" + String.format(" [%tT]", Calendar.getInstance()));
        }
    }

    private void update(long init, Calendar startTime, long from, long to, Bounds bound) {
        long took;
        took = System.currentTimeMillis() - init;
        totalOfThreadsRunning.decrementAndGet();
        if (secretIsClosed()) {

            if (verbose) {
                System.out.println(String.format("[%tT]", startTime) + bound + " " + Thread.currentThread().getName() + " KEY NOT FOUND IN [" + from + "," + to + "] [still running " + totalOfThreadsRunning.get() + " threads]" + "[took " + took / 1000 + " s]" + String.format(" [%tT]", Calendar.getInstance()));
            }

        } else if (Thread.currentThread() != theFounderThread) {

            if (verbose) {
                System.out.println(String.format("[%tT]", startTime) + bound + " " + Thread.currentThread().getName() + " STOP SEARCHING : ANOTHER THREAD HAS FOUND THE KEY " + new String(binaryKey) + " [still running " + totalOfThreadsRunning.get() + " threads]" + "[took " + took / 1000 + " s]" + String.format(" [%tT]", Calendar.getInstance()));
            }

        }
    }

    public boolean binaryKeyOpensSecret(char[] binaryKey) {

        for (char bit : binaryKey) {
            if (bit == '0') {
                doMoveOne(initialSB);
            } else {
                doMoveTwo(initialSB);
            }
        }

        //After applying a combination of movesOne and movesTwo to the initial string,
        //checks if initial string has turned equals to the target string
        boolean matches = initialSB.toString().equals(target);

        //Reverses initialSB to initial value
        initialSB.delete(0, initialSB.length());
        initialSB.append(this.initial);

        return matches;

    }

    static public boolean hasFinishedAnalysing() {
        synchronized (lock) {
            return totalOfThreadsRunning.get() == zero.get();
        }
    }

    static public boolean canObtainTargetFromInitialString() {
        return !secretIsClosed();
    }

    static void incrementThreadCount() {
        totalOfThreadsRunning.incrementAndGet();
    }

    static void decrementThreadCount() {
        totalOfThreadsRunning.decrementAndGet();
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

    static public char[] reverse(char[] chars) {

        int length = chars.length;

        char[] reversed = Arrays.copyOf(chars, length);

        for (int i = 0; i < length; i++) {
            reversed[i] = chars[length - i - 1];
        }

        return reversed;
    }


    static public void analyseInSingleThread(String initial, String target, Bounds bound) {

        binaryKey = new char[0];

        long totalOfCombinations = (long) Math.pow(2, target.length() - initial.length());

        long start = 0;
        long end = 0;
        long step = 0;

        if (bound.equals(Bounds.BOTTOM)) {

            start = 0;
            if ((totalOfCombinations / 2) - 2 >= 0) {
                start = (totalOfCombinations / 2) - 2;
            }

            end = 0;
            step = 2;

        } else {

            start = totalOfCombinations - 1;
            end = totalOfCombinations / 2;
            step = 1;

        }

        StringBuilder initialSB = new StringBuilder(initial);

        for (long key = start; key >= end; key -= step) {

            char[] binaryKey = Long.toBinaryString(key).toCharArray();
            if (bound.equals(Bounds.BOTTOM)) {
                binaryKey = completeLeftZeroes(binaryKey, target.length() - initial.length());
            }
            if (binaryKeyOpensSecret(binaryKey, initialSB, initial, target)) {
                Analyzer.binaryKey = binaryKey;
                break;
            }
            if (bound.equals(Bounds.UPPER)) {
                if (key % 2 == 0) {
                    binaryKey = reverse(binaryKey);
                    if (binaryKeyOpensSecret(binaryKey, initialSB, initial, target)) {
                        Analyzer.binaryKey = binaryKey;
                        break;
                    }
                }
            }

        }
    }


    static public synchronized void reset() {

        theFounderThread = null;
        totalOfThreadsRunning = new AtomicLong(0);
        binaryKey = new char[0];

    }

    static public void describeHowKeyOpensSecret(char[] key, String initial_, String target) {

        StringBuilder initial = new StringBuilder(initial_);

        System.out.println("\n\t\t-------------------------------------------------------------------------------------------------------------------------------------------------\n");
        System.out.println("\t\tdescribing operations (binary key) " + new String(key) + " on initial string " + initial + " to obtain target string " + target + " -------------------------------------");
        System.out.println("\n\t\t0/1 \t\tmeaning\t\t\t\tresults\t\t\ttarget\n");

        for (char bit : key) {
            if (bit == '0') {
                doMoveOne(initial);
                System.out.println("\t\t 0" + "\tadds  A to the end of the string \t" + initial + "\t\t\t" + target);

            } else {
                doMoveTwo(initial);
                System.out.println("\t\t 1" + "\treverses s and adds B to  s' end  \t" + initial + "\t\t\t" + target);
            }
        }
        System.out.println("\n\t\t-------------------------------------------------------------------------------------------------------------------------------------------------\n\n");

    }

    static public boolean binaryKeyOpensSecret(char[] binaryKey, StringBuilder initialSB, String initial, String target) {

        for (char bit : binaryKey) {
            if (bit == '0') {
                doMoveOne(initialSB);
            } else {
                doMoveTwo(initialSB);
            }
        }

        //After applying a combination of movesOne and movesTwo to the initial string,
        //checks if initial string has turned equals to the target string
        boolean matches = initialSB.toString().equals(target);

        //Reverses initialSB to initial value
        initialSB.delete(0, initialSB.length());
        initialSB.append(initial);

        return matches;

    }

    static public char[] completeLeftZeroes(char[] binary, int maxLength) {

        if(maxLength<binary.length) return binary;
        char[] completed = Arrays.copyOf(binary, maxLength);

        for (int i = 0; i < maxLength - binary.length; i++) {
            completed[i] = '0';
        }
        int j = 0;
        for (int i = maxLength - binary.length; i < maxLength; i++) {
            completed[i] = binary[j++];
        }

        return completed;

    }

    static public void doMoveOne(StringBuilder initial) {
        initial.append("A");
    }

    static public void doMoveTwo(StringBuilder initialSB) {
        initialSB.reverse().append("B");
    }

}
