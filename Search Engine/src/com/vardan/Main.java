package com.vardan;


import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        //String current_directory = System.getProperty("user.dir");
        //System.out.println("Current wrd - "+current_directory);
        CreateIndex.Creating_Index();
        SearchEngine.Engine();
        System.out.println("Searching Successful");
    }
}
