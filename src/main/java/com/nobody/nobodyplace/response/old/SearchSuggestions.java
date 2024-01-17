package com.nobody.nobodyplace.response.old;

import com.nobody.nobodyplace.response.old.Data;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestions extends Data {

    public long seq = 0;
    public String input = "";
    public List<String> suggestions = new ArrayList<>();

}
