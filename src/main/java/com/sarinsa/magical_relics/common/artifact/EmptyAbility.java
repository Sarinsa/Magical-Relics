package com.sarinsa.magical_relics.common.artifact;

public class EmptyAbility extends BaseArtifactAbility {

    private static final String[] NO_PREFIXES = {""};
    private static final String[] NO_SUFFIXES = {""};

    public EmptyAbility() {
        super("empty");
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.PASSIVE;
    }

    @Override
    public String[] getPrefixes() {
        return NO_PREFIXES;
    }

    @Override
    public String[] getSuffixes() {
        return NO_SUFFIXES;
    }
}
