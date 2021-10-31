package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Wild;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Wild_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

public class Wild_X2_Gem extends Wild_Gem {

    @Override
    public GemType getDisplayGemType() {
        return GemType.WILD_X2;
    }

    @Override
    public int getMultiplier() {
        return 2;
    }
}
