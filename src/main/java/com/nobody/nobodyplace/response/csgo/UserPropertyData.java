package com.nobody.nobodyplace.response.csgo;

import com.nobody.nobodyplace.entity.csgo.CsgoItem;
import com.nobody.nobodyplace.response.Data;

import java.util.ArrayList;
import java.util.List;

public class UserPropertyData extends Data {
    public List<CsgoItem> properties = new ArrayList<>();
}
