package pacman.game;

import java.util.Objects;
import pacman.game.Constants.MOVE;

public final class StateAction {
    private final int state;
    private final MOVE action;

    public StateAction(int state, MOVE action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public int hashCode() {
        // Override the hashCode method for using StateAction as a key in the Q-table
        return Objects.hash(state, action);
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

