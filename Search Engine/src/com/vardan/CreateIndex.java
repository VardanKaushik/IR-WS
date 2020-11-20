package com.vardan;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;


public class CreateIndex {

    //Directory where the search index will be stored
    public static String Index_Directory = "../index";

    public static void Creating_Index() throws IOException {
        //Analyzer that is used to process Text Field
        Analyzer analyzer = new StandardAnalyzer();

        //To store index on disc
        Directory directory = FSDirectory.open(Paths.get(Index_Directory));

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        //Create a new index
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter iwriter = new IndexWriter(directory,config);

        iwriter = add_docs(iwriter);
        iwriter.close();
        directory.close();
    }

    public static IndexWriter add_docs(IndexWriter index_writer) throws IOException {
        //System.out.println("No errors till now");
        String doc_Path="cran/cran.all.1400";
        //String doc_Path = "../cran/cran.all.1400";

        FileReader file_reader = new FileReader(doc_Path);
        BufferedReader bufferedReader = new BufferedReader(file_reader);

        String line="";
        int index = 0;

        line = bufferedReader.readLine();

        String title,author,content,publication;

        while(line!=null)
        {
            //System.out.println(line);
            index++;
            title = "";
            author = "";
            content = "";
            publication = "";

            if(line.contains(".I"))
                line = bufferedReader.readLine();

            if(line.contains(".T"))
            {
                line = bufferedReader.readLine();

                while(!line.contains(".A"))
                {
                    title = title + line + " ";
                    line = bufferedReader.readLine();
                }
            }

            if(line.contains(".A"))             //A can be multiple lines as well
            {
                line = bufferedReader.readLine();
                if(!line.contains(".B"))        //A not empty
                {
                    while(!line.contains(".B"))
                    {
                        author = author + line + " ";
                        line = bufferedReader.readLine();
                    }
                }
            }

            if(line.contains(".B"))
            {
                line = bufferedReader.readLine();
                if(!line.contains(".W"))        //meaning B is not empty
                {
                    while(!line.contains(".W"))
                    {
                        publication = publication + line + " ";
                        line = bufferedReader.readLine();
                    }
                }

            }

            if(line.contains(".W"))
            {
                line = bufferedReader.readLine();
                while(!line.contains(".I"))
                {
                    content = content + line + " ";
                    line = bufferedReader.readLine();
                    if(line==null)
                        break;                      //exists line.contains(.I) loop
                }
            }

            //System.out.println(line);

            Document doc = new Document();
            doc.add(new TextField("index",index+"",Field.Store.YES));
            doc.add(new TextField("title",title,Field.Store.YES));
            doc.add(new TextField("author",author,Field.Store.YES));
            //doc.add(new TextField("publication",publication,Field.Store.YES));
            doc.add(new TextField("content",content,Field.Store.YES));
            doc.add(new TextField("cranID",index+"", Field.Store.YES));

            index_writer.addDocument(doc);
        }//end of while loop

        bufferedReader.close();
        return index_writer;

    }

}
