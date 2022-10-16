package model;

import javafx.util.converter.LocalDateStringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Email implements Comparable<Email>{
    LocalDate date;
    String sender;
    String header;
    String content;

    public Email(String date, String sender){
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.sender = sender;
    }
    public Email(String date, String sender, String header, String content){
        this(date, sender);
        this.header = header;
        this.content = content;
    }

    public String toString(){
        return "Date: "+date+"\nSender: "+sender+"\nHeader: "+header+"\nContent: "+content;
    }

    @Override
    public int compareTo(Email o) {
        Email email = o;
        if(this.date.compareTo(email.date) < 0) return -1;
        if(this.date.compareTo(email.date) > 0) return 1;
        return Integer.compare(this.sender.compareTo(email.sender), 0);
    }
}
