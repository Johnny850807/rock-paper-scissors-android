package com.afinal.youjinchen.rock_paper_scissors;


public abstract class Player {
    private String name;

    public Player(){
        this.name = getName();
    }

    public abstract String getName();
    public abstract Fist decide();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;

        Player player = (Player) o;

        return name != null ? name.equals(player.name) : player.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
