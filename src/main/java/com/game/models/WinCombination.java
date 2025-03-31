package com.game.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WinCombination {
    @JsonProperty("reward_multiplier")
    private double reward_multiplier;
    private String when;
    private int count;
    private String group;
    private String[][] covered_areas;

    // Getters and setters
    public double getRewardMultiplier() { return reward_multiplier; }
    public void setRewardMultiplier(double reward_multiplier) { this.reward_multiplier = reward_multiplier; }
    public String getWhen() { return when; }
    public void setWhen(String when) { this.when = when; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
    public String[][] getCoveredAreas() { return covered_areas; }
    public void setCoveredAreas(String[][] covered_areas) { this.covered_areas = covered_areas; }
}