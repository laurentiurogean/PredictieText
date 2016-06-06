package com.example.laurentiu.predictietext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Laurentiu on 08.05.2016.
 */
public class TextHandler {
    public void saveTxtFile (ArrayList array, String fileName) {

    }

    public ArrayList fetchGrams(ArrayList<HashMap> ngrams, String word, String secondWord) {

        // The ArrayList that has words starting with the word typed
        ArrayList<HashMap> intermediaryArrayList = new ArrayList<>();

        // The ArrayList containing up to 3 most used words
        ArrayList<HashMap> resultsArrayList = new ArrayList<>();

        for(HashMap hm:ngrams)
            if(((String)hm.get("gram")).startsWith(word)) {
                if (secondWord != null) {
                    String intermediaryWord = "";
                    for(int i=0; i<((String) hm.get("gram")).length()-1; i++)
                        if(((String) hm.get("gram")).charAt(i) == ' ') {
                            intermediaryWord = ((String) hm.get("gram")).substring(i+1, ((String) hm.get("gram")).length());
                            intermediaryWord.replace(" ", "");
                            break;
                        }
                    // TO-DO cauta in unigrams daca nu gaseste
                    if(intermediaryWord.startsWith(secondWord.toString()))
                        intermediaryArrayList.add(hm);
                } else intermediaryArrayList.add(hm);
            }

        Collections.sort(intermediaryArrayList, new MyMapComparator());
        if(intermediaryArrayList.size() >= 3) {
            resultsArrayList.add((HashMap) intermediaryArrayList.get(intermediaryArrayList.size() - 1));
            resultsArrayList.add((HashMap) intermediaryArrayList.get(intermediaryArrayList.size() - 2));
            resultsArrayList.add((HashMap) intermediaryArrayList.get(intermediaryArrayList.size() - 3));
        } else {
            for(HashMap hm:intermediaryArrayList)
                resultsArrayList.add(hm);
        }
        return resultsArrayList;
    }

    public ArrayList fetchBigramWords(ArrayList<HashMap> ngrams, String word, String secondWord) {
        ArrayList<String> bigramWords = new ArrayList();
        ArrayList<HashMap> bigrams = fetchGrams(ngrams, word, secondWord);

        for(HashMap hm:bigrams) {
            String words = (String) hm.get("gram");
            for(int i=0; i< words.length()-1; i++)
                if(words.charAt(i) == ' ') {
                    words = words.substring(i);
                    bigramWords.add(words);
                }
        }
        return bigramWords;
    }

    public class MyMapComparator implements Comparator<HashMap>
    {
        @Override
        public int compare(HashMap lhs, HashMap rhs) {
            return (int) lhs.get("frequency") - (int) rhs.get("frequency");
        }
    }
}
