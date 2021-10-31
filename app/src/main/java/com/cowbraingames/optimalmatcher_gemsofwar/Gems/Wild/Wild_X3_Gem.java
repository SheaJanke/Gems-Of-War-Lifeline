package com.cowbraingames.optimalmatcher_gemsofwar.Gems.Wild;

import com.cowbraingames.optimalmatcher_gemsofwar.Gems.Generic.Wild_Gem;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

public class Wild_X3_Gem extends Wild_Gem {
    @Override
    public GemType getDisplayGemType() {
        return GemType.WILD_X3;
    }

    @Override
    public int getMultiplier() {
        return 3;
    }
}
