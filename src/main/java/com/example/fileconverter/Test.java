package com.example.fileconverter;

import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        String time = String.format("%d-%02d-%d-%d-%d", now.getYear() ,now.getMonthValue() , now.getDayOfMonth(), now.getHour(),now.getNano());
        System.out.println(time);
    }
}
