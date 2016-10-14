/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.undra.abba.bruteforce;

/**
 *
 * @author alexandre
 */
class Packet {

    public final long from;
    public final long to;
    public Thread thread;

    public Packet(long from, long to) {
        this.from = from;
        this.to = to;
    }
       
}