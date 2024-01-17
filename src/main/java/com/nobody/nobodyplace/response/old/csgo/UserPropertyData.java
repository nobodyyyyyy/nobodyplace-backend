package com.nobody.nobodyplace.response.old.csgo;

import com.nobody.nobodyplace.oldpojo.entity.csgo.CsgoItem;
import com.nobody.nobodyplace.response.old.Data;

import java.util.ArrayList;
import java.util.List;

public class UserPropertyData extends Data {
    public List<CsgoItem> properties = new ArrayList<>();
}
