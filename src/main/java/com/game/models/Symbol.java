package com.game.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Symbol {
    @JsonProperty("reward_multiplier")
    private Double reward_multiplier;
    private String type;
    private String impact;
    private Double extra;

    // Getters and setters
    public Double getRewardMultiplier() { return reward_multiplier; }
    public void setRewardMultiplier(Double reward_multiplier) { this.reward_multiplier = reward_multiplier; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
    public Double getExtra() { return extra; }
    public void setExtra(Double extra) { this.extra = extra; }
}