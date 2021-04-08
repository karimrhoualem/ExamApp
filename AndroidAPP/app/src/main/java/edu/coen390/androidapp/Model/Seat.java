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
    Map<Integer, OCCUPANCY> seatState;
    Map<Integer, Integer> studentSeat;

    public Seat(int totalSeatsNumber) {
        this.totalSeatsNumber = totalSeatsNumber;
        seats = new LinkedList<>();
        seatState = new Hashtable<>();
        studentSeat = new Hashtable<>();
        generateSeatNumbers();
    }

    public void generateSeatNumbers() {
        fillRandomNumbers();
        shuffle();
        initializeSeats();
    }

    public void fillRandomNumbers() {
        for (int i = 0; i < totalSeatsNumber; i++) {
            seats.add(i + 1);
        }
    }

    public void shuffle() {
        Collections.shuffle(seats);
    }

    public void initializeSeats() {
        for (int num : seats) {
            seatState.put(num, OCCUPANCY.EMPTY);
        }
    }

    public void setSeatOccupied(int seatNumber) {
        seatState.put(seatNumber, OCCUPANCY.OCCUPIED);
    }

    public void setSeatEmpty(int seatNumber) {
        seatState.put(seatNumber, OCCUPANCY.OCCUPIED);
    }

    public int getNextSeat(Student student) {
        if (!isFull()) {
            for (int seat : seats) {
                if (!isOccupied(seat)) {
                    if(studentSeat.get((int)student.getId()) == null){
                        setSeatOccupied(seat);
                        studentSeat.put((int)student.getId(), seat);
                        return seat;
                    }
                    return studentSeat.get((int)student.getId());
                }
            }
        }
        return -1;
    }

    public Map<Integer, Integer> getStudentSeat() {
        return studentSeat;
    }

    public boolean isOccupied(int seatNumber) {
        if (!seatState.isEmpty()) {
            if (seatState.get(seatNumber) == OCCUPANCY.EMPTY) {
                return false;
            }
            else if (seatState.get(seatNumber) == OCCUPANCY.OCCUPIED) {
                return true;
            }
        }
        return true;
    }

    public boolean isFull() {
        for (int seat : seats)
            if (seatState.get(seat) == OCCUPANCY.EMPTY) {
                return false;
            }
        return true;
    }

    enum OCCUPANCY {
        OCCUPIED,
        EMPTY
    }
}
