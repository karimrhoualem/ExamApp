package edu.coen390.androidapp.Model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Seat implements Serializable {
    int totalSeatsNumber;
    List<Integer> seats;
    Map<Integer,OCCUPANCY> seatState;

    public Seat(int totalSeatsNumber) {
        this.totalSeatsNumber = totalSeatsNumber;
        seats = new LinkedList<>();
        seatState = new Hashtable<>();
        generateSeatNumbers();
    }

    enum OCCUPANCY {
        OCCUPIED,
        EMPTY
    }

    public void generateSeatNumbers() {
       fillRandomNumbers();
       shuffle();
       initializeSeats();
    }

    public void fillRandomNumbers(){
        for(int i = 0; i < totalSeatsNumber; i++){
            seats.add(i+1);
        }
    }

    public void shuffle(){
        Collections.shuffle(seats);
    }

    public void initializeSeats(){
        for(int num: seats){
            seatState.put(num,OCCUPANCY.EMPTY);
        }
    }

    public void setSeatOccupied(int seatNumber){
        seatState.replace(seatNumber, OCCUPANCY.OCCUPIED);
    }

    public void setSeatEmpty(int seatNumber){
        seatState.replace(seatNumber, OCCUPANCY.OCCUPIED);
    }

    public int getNextSeat(){
       if(!isFull()){
           for(int seat: seats){
               if(!isOccupied(seat)){
                   setSeatOccupied(seat);
                   return seat;
               }
           }
       }
       return -1;
    }

    public boolean isOccupied(int seatNumber){
       if(!seatState.isEmpty())
           return !Objects.equals(seatState.get(seatNumber), OCCUPANCY.EMPTY);
        return true;
    }

    public boolean isFull(){
        int totalOccupied = 0;
        for(int seat: seats)
            if (seatState.get(seat) == OCCUPANCY.OCCUPIED)
                totalOccupied++;
        return totalOccupied == totalSeatsNumber;
    }


}
