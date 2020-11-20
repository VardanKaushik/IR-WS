package com.vardan;


import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
	// write your code here
        String current_directory = System.getProperty("user.dir");
        System.out.println("Current wrd - "+current_directory);
        CreateIndex.Creating_Index();
        SearchEngine.Engine();
    }
}
