package com.example.laurentiu.predictietext;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by Laurentiu on 08.04.2016.
 */
public class CorpusParser {

    public enum Categories {
        Hotel,
        Spital,
        Cumparaturi
    };

    /**
     * Reads the initial text file and removes the punctuation.
     */
    public String readFromAssets(Context context, Categories category) throws IOException {
        BufferedReader reader = null;

        // This switch holds changes the input for each category
        switch (category) {
            case Hotel:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.ch)));
                break;
            case Spital:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.medicalcorpus)));
                break;
            case Cumparaturi:
                break;
            default:
                reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.medicalcorpus)));
        }

        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            if(!mLine.startsWith("#") && !mLine.startsWith("M:")) {
                mLine = mLine.replace("P: ", "");
                sb.append(mLine); // process line
            }
            mLine = reader.readLine();
        }
        reader.close();
        String text = sb.toString();
        text = text.replace("?", " ");
        text = text.replace(".", " ");
        text = text.replace(",", " ");
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

        String allLowerCase = input.toLowerCase();
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
        return finalWordList;
    }
}
