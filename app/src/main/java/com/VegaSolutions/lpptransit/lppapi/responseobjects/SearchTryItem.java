package com.VegaSolutions.lpptransit.lppapi.responseobjects;

import java.util.Date;

public class SearchTryItem {

    private final String searchItemId;
    private final Date date;

    public SearchTryItem(String searchItemId) {
        this.searchItemId = searchItemId;
        this.date = new Date();
    }

    public String getSearchItemId() {
        return searchItemId;
    }

    public Date getDate() {
        return date;
    }
}
