package com.example.fileconverter;

import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) {
        String time = LocalDateTime.now().toString().replace(":","-").replace(".","-");
        System.out.println(time);
    }
}
