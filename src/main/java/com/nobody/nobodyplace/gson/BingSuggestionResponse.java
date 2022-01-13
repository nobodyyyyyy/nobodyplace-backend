package com.nobody.nobodyplace.gson;

import java.util.List;

public class BingSuggestionResponse {

    public Body AS;

    public class Body {
        public String Query;
        public String FullResults;
        public List<ResultNode> Results;
    }

    public class ResultNode {
        public String Type;
        public List<Suggestion> Suggests;
    }

    public class Suggestion {
        public String Txt;
        public String Type;
        public String Sk;
        public int HCS;
    }
}
