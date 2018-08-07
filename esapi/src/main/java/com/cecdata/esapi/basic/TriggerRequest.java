package com.cecdata.esapi.basic;

public class TriggerRequest {

    private String esUrl;
    private String esColumn;
    private String queryWord;
    private int benchMark;
    private String user;

    public String getEsUrl() {
        return esUrl;
    }

    public void setEsUrl(String esUrl) {
        this.esUrl = esUrl;
    }

    public String getEsColumn() {
        return esColumn;
    }

    public void setEsColumn(String esColumn) {
        this.esColumn = esColumn;
    }

    public String getQueryWord() {
        return queryWord;
    }

    public void setQueryWord(String queryWord) {
        this.queryWord = queryWord;
    }

    public int getBenchMark() {
        return benchMark;
    }

    public void setBenchMark(int benchMark) {
        this.benchMark = benchMark;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
