package edu.neu.madcourse.joeyhuang.finalproject;

public class HomeScreenModel{

    private int icon;
    private String title;
    private String counter;
    public boolean isGroupHeader = false;

    public HomeScreenModel(String title) {
        this(-1,title,null);
        isGroupHeader = true;
    }
    public HomeScreenModel(int icon, String title, String counter) {
        super();
        this.icon = icon;
        this.title = title;
        this.counter = counter;
    }

    public boolean isGroupHeader() {
        return isGroupHeader;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getCounter() {
        return counter;
    }

}