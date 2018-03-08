package com.crumet.diction.model;

/**
 * Created by ravi on 2/25/2018.
 */

public class Results {
    private String word, meaning, example, partOfSpeech;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public Results() {
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public Results(String word, String meaning, String example, String partOfSpeech) {

        this.word = word;
        this.meaning = meaning;
        this.example = example;
        this.partOfSpeech = partOfSpeech;
    }
}
