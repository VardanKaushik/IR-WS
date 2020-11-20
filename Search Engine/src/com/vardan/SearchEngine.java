package com.vardan;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class SearchEngine
{
    public static String Index_Directory = "../index";
    static int MAX_RESULTS = 10;
    public static String Output_Directory = "../output";

    static Directory directory;

    static {
        try {
            directory = FSDirectory.open(Paths.get(Index_Directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static DirectoryReader ireader;

    static {
        try {
            ireader = DirectoryReader.open(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static IndexSearcher isearcher = new IndexSearcher(ireader);

    static Analyzer analyzer = new StandardAnalyzer();

    public static void Engine() throws IOException, ParseException {
        Similarity similarity_object = new BM25Similarity();
        isearcher.setSimilarity(similarity_object);

        ArrayList query_index = indexing_queries();
        ArrayList<ArrayList<String[]>> query_results = searching(query_index);

        File file = new File(Output_Directory);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

        for(ArrayList<String[]> res: query_results) {
            for(String[] info: res) {
                for(String temp: info) {
                    if(!temp.contains("STANDARD"))
                        bufferedWriter.write(temp + " ");
                    else
                        bufferedWriter.write(temp);
                }
                bufferedWriter.newLine();
            }
        }

        bufferedWriter.close();
    }

    public static ArrayList indexing_queries() throws IOException {
        String query_path = "cran/cran.qry";
        HashMap hash_map = new HashMap();
        ArrayList array_list = new ArrayList();

        File file = new File(query_path);
        FileReader file_reader = new FileReader(file);

        BufferedReader bufferedReader = new BufferedReader(file_reader);

        String line="";
        String query_data="";
        int index=0;
        line=bufferedReader.readLine();

        while(line!=null)
        {
            index++;
            query_data="";
            hash_map = new HashMap();

            if(line.contains(".I"))
                line=bufferedReader.readLine();

            if(line.contains(".W"))
            {
                line=bufferedReader.readLine();
                while(!line.contains(".I"))
                {
                    query_data = query_data +  line + " ";
                    line=bufferedReader.readLine();
                    if(line==null)
                        break;
                }
            }

            query_data = query_data.replace("?","");
            query_data = query_data.replace("*","");

            hash_map.put("ID",Integer.toString(index));
            hash_map.put("Query",query_data);

            array_list.add(hash_map);
            //System.out.println(index);
        }

        //array_list.add(hash_map);
        //System.out.println(hash_map);

        file_reader.close();

        return  array_list;

    }

    public static ArrayList<ArrayList<String[]>> searching(ArrayList query_index) throws IOException, ParseException {
        ArrayList<ArrayList<String[]>> results = new ArrayList();
        String query_ID,query_data;

        for(int index = 0; index<query_index.size(); index++)
        {
            HashMap temp_hash = (HashMap) query_index.get(index);
            query_ID = (String) temp_hash.get("ID");
            query_data = (String) temp_hash.get("Query");

            ArrayList query_result = search(query_ID,query_data);

            results.add(query_result);
        }
        return results;
    }

    public static ArrayList<String[]> search(String ID, String query_question) throws IOException, ParseException {
        String columns[]=new String[]{"cranID","title","author","content","index"};

        QueryParser query_parser = new MultiFieldQueryParser(columns,analyzer);

        ArrayList<Integer> document_IDs = new ArrayList();
        ArrayList<String[]> document_ranking = new ArrayList<>();

        Query query = query_parser.parse(query_question);

        ScoreDoc[] hits = isearcher.search(query,MAX_RESULTS).scoreDocs;

        int ranking = 1;

        for(ScoreDoc hit:hits)
        {
            Document doc = isearcher.doc(hit.doc);

            String str_ID = doc.get("cranID");
            String splits[] = str_ID.split(" ",0);
            String q_IDs[] = ID.split(" ",0);
            int q_ID = Integer.parseInt(q_IDs[0]);
            int id = Integer.parseInt(splits[0]);

            document_IDs.add(id);
            String[] info = {""+q_ID,"0",String.valueOf(id),
            Integer.toString(ranking++),Float.toString(hit.score),
            "STANDARD"};

            document_ranking.add(info);
        }
        return document_ranking;
    }

    public SearchEngine() throws IOException {
    }
}
