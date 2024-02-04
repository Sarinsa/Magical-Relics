package com.sarinsa.magical_relics.common.artifact;

public class OreRadarAbility extends BaseArtifactAbility {

    public OreRadarAbility(String abilityName) {
        super(abilityName);
    }

    @Override
    public String[] getPrefixes() {
        return new String[0];
    }

    @Override
    public String[] getSuffixes() {
        return new String[0];
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.ARMOR;
    }
}
