package edu.coen390.androidapp.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class Seat {
    int totalSeats;
    int seatNumber;
    List<Integer> randomNumbers;
    Map<Integer,OCCUPANCY> seatState;

    public Seat(int totalSeats) {
        this.totalSeats = totalSeats;
        randomNumbers = new LinkedList<>();
        seatState = new Hashtable<>();
    }

    enum OCCUPANCY {
        OCCUPIED,
        EMPTY
    }

    public void generateSeatNumbers() {
       fillRandomNumbers();
       shuffle();
       setSeatState();
    }

    public void fillRandomNumbers(){
        for(int i = 0; i < totalSeats; i++){
            randomNumbers.add(i+1);
        }
    }

    public void shuffle(){
        Collections.shuffle(Collections.singletonList(randomNumbers));
    }

    public void setSeatState(){
        for(int num: randomNumbers){
            seatState.put(num,OCCUPANCY.EMPTY);
        }
    }



}
