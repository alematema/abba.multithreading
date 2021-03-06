package br.edu.undra.abba.model;

import br.edu.undra.abba.bruteforce.SingleThreadedBruteForce;
import br.edu.undra.abba.bruteforce.MultiThreadedBruteForce;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author alexandre
 */
public class ABBA {

    public String canObtain(String initial, String target) {

        if (initial == null || target == null) {
            return "Impossible";
        }

        if (initial.length() >= target.length()) {
            return "Impossible";
        }

        initial = initial.toUpperCase();
        target = target.toUpperCase();

        for (int i = 0; i < target.length(); i++) {
            if (target.charAt(i) != 'A' && target.charAt(i) != 'B') {
                return "Impossible";
            }
        }

        for (int i = 0; i < initial.length(); i++) {
            if (initial.charAt(i) != 'A' && initial.charAt(i) != 'B') {
                return "Impossible";
            }
        }

        boolean runInSingleThreadPacketsSmallerThan4194304Size = true;//packet size = 2 raised to the power of (target.length() - initial.length())

        String canObtain = "";

        if ((long) Math.pow(2, target.length() - initial.length()) > 4194304l | !runInSingleThreadPacketsSmallerThan4194304Size) {
            canObtain = new MultiThreadedBruteForce(initial, target).tryObtainingTargetFromInitial();
        } else {
            canObtain = new SingleThreadedBruteForce(initial, target).tryObtainingTargetFromInitial();
        }

        return canObtain;

    }

    public static void main(String[] args) {
        ABBA abba = new ABBA();
        long init = System.nanoTime();
        System.out.println(abba.canObtain("BBBBABABBBBB", "BBBBABABBABBBBBBABABBBBBBBBABAABBBAA"));
        System.out.println("\ntook " + (System.nanoTime() - init) / 1000000 + " mili secs");
        System.out.println("took " + (System.nanoTime() - init) + " nano secs");
    }

}
