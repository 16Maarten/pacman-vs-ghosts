package pacman.game;

import pacman.game.Constants.MOVE;

public class StateAction {
    private final int state;
    private final MOVE action;

    public StateAction(int state, MOVE action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public int hashCode() {
        // Override the hashCode method for using StateAction as a key in the Q-table
        return state * 31 + action.ordinal();
    }

    @Override
    public boolean equals(Object obj) {
        // Override the equals method for comparing StateAction objects
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StateAction other = (StateAction) obj;
        return state == other.state && action == other.action;
    }
}

