package com.example.android.cipherchat.Objects;

public class Tutorial {

    private String title;
    private String tutorialContents;

    public Tutorial() {
        this.title = "";
        this.tutorialContents = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTutorialContents() {
        return tutorialContents;
    }

    public void setTutorialContents(String tutorialContents) {
        this.tutorialContents = tutorialContents;
    }
}
