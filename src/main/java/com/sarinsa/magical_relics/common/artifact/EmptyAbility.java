package com.sarinsa.magical_relics.common.artifact;

public class EmptyAbility extends BaseArtifactAbility {

    public EmptyAbility() {
        super("empty");
    }

    @Override
    public TriggerType getTriggerType() {
        return TriggerType.INVENTORY;
    }
}
