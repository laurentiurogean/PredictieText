package com.example.laurentiu.predictietext;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by Laurentiu on 08.04.2016.
 */
public class CorpusParser {
    /**
     * Enumeration that holds the categories/locations
     */
    public enum Categories {
        Hotel,
        Spital,
        Cumparaturi,
        Tribunal,
        Sport,
        Teatru
    };

    /**
     * Reads the initial text file and removes the punctuation.
     */
    public String readFromAssets(Context context, Categories category) throws IOException {
        BufferedReader reader = null;

        // This switch holds changes the input for each category
        switch (category) {
            case Hotel:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.hotel)));
                break;
            case Spital:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.medic)));
                break;
            case Cumparaturi:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.cumparaturi)));
                break;
            case Tribunal:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.tribunal)));
                break;
            case Sport:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.sport)));
                break;
            case Teatru:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.teatru)));
                break;
            default:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.medic)));
        }

        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            if(!mLine.startsWith("#") && !mLine.startsWith("M:")) {
                mLine = mLine.replace("P: ", " ");
                mLine = mLine.replace("?", "");
                mLine = mLine.replace(":", "");
                mLine = mLine.replace("„", "");
                mLine = mLine.replace(".", "");
                mLine = mLine.replace(",", "");
                mLine = mLine.replace("!", "");
                sb.append(mLine + " "); // process line
            }
            mLine = reader.readLine();
        }
        reader.close();
        String text = sb.toString();
        return text;
    }

    /**
     * Returns an ArrayList containing HashMaps with every unigram and its frequency
     */
    protected ArrayList<HashMap> computeUnigrams(String input) {

        // The ArrayList in which we'll first add the tokens
        ArrayList<String> tokens = new ArrayList<String>();

        // The ArrayList used to get rid of the duplicates ~ could find a better method
        ArrayList<String> tokensTakenOnce = new ArrayList<String>();

        // The final ArrayList which will be returned
        ArrayList<HashMap> finalWordList = new ArrayList<HashMap>();

        String allLowerCase = input.toLowerCase(); // Make all letters lower case
        StringTokenizer strTokenizer = new StringTokenizer(allLowerCase, " ");
        while(strTokenizer.hasMoreTokens()) tokens.add(strTokenizer.nextToken());

        // Iterate through all the tokens
        for (String gram:tokens) {
            int frequency = 1;

            // Check if this word was seen previously
            if(tokensTakenOnce.contains(gram)) {
                for(HashMap map:finalWordList) // If YES then update its frequency
                    if(map.get("gram").equals(gram)) {
                        int freq = (int) map.get("frequency");
                        map.put("frequency", freq+1);
                    }
            } else { // If not then add it with initial frequency
                tokensTakenOnce.add(gram);
                HashMap wordhm = new HashMap();
                wordhm.put("gram", gram);
                wordhm.put("frequency", frequency);
                finalWordList.add(wordhm);
            }
        }
        return finalWordList;
    }

    /**
     * Returns an ArrayList containing HashMaps with every bigram and its frequency
     */
    protected ArrayList<HashMap> computeBigrams(String input) {

        // The ArrayList in which we'll first add the tokens
        ArrayList<String> tokens = new ArrayList<String>();

        // The ArrayList used to get rid of the duplicates ~ could find a better method
        ArrayList<String> tokensTakenOnce = new ArrayList<String>();

        // The final ArrayList which will be returned
        ArrayList<HashMap> finalWordList = new ArrayList<HashMap>();

        String allLowerCase = input.toLowerCase();
        StringTokenizer strTokenizer = new StringTokenizer(allLowerCase, " ");
        while(strTokenizer.hasMoreTokens()) tokens.add(strTokenizer.nextToken());

        // Iterate through all the tokens
        for (int i=0; i<tokens.size()-1; i++) {
            String bigram = tokens.get(i) + " " + tokens.get(i+1); // Form a bigram
            int frequency = 1;

            // Check if this word was seen previously
            if(tokensTakenOnce.contains(bigram)) {
                for(HashMap map:finalWordList) // If YES then update its frequency
                    if(map.get("gram").equals(bigram)) {
                        int freq = (int) map.get("frequency");
                        map.put("frequency", freq+1);
                    }
            } else { // If not then add it with initial frequency
                tokensTakenOnce.add(bigram);
                HashMap gramHm = new HashMap();
                gramHm.put("gram", bigram);
                gramHm.put("frequency", frequency);
                finalWordList.add(gramHm);
            }
        }

//        int max = 0;
//        String word = "";
//        for(HashMap hm:finalWordList) {
//            int frequency = (int) hm.get("frequency");
//            String gram = (String) hm.get("gram");
//            if(frequency > 50) {
//                max = frequency;
//                word = gram;
//            }
//        }
//        Log.d("Max", "max is" + word);
        return finalWordList;
    }

    public void addWordToFile(String word, String fileName, Context context) {
        try
        {
//            File file = new File(Environment.getRootDirectory() + );
            FileWriter fw = new FileWriter(fileName, true); //the true will append the new data
            fw.write("\n word");//appends the string to the file
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: Cannot append to file!" + ioe);
        }
    }
}
