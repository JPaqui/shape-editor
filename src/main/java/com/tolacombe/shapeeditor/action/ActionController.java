package com.tolacombe.shapeeditor.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionController {
    private final List<Action> listEvents;
    private int pointerIndex = 0;

    public ActionController(Action initialState) {
        listEvents = new ArrayList<>();
        listEvents.add(initialState);
    }

    /**
     * Create a save action.<br>
     * P=0, list=[A]     -> P=1, list=[A,B]<br>
     * P=1, list=[A,B]   -> P=2, list=[A,B,C]<br>
     * P=0, list=[A,B]   -> P=1, list=[A,C]<br>
     * P=2, list=[A,B,C] -> P=3, list=[A,B,C]<br>
     * P=1, list=[A,B,C] -> P=2, list=[A,C]<br>
     * P=0, list=[A,B,C] -> P=1, list=[C]<br>
     * @param action the action
     */
    public void saveAction(Action action) {
        listEvents.add(++pointerIndex, action);
        while(listEvents.size()-1 > pointerIndex) {
            listEvents.remove(listEvents.size()-1);
        }
    }

    /**
     * Undo an action.<br>
     * P=0, list=[A]     -> return empty<br>
     * P=1, list=[A,B]   -> return A, P=0<br>
     * P=0, list=[A,B]   -> return empty<br>
     * P=2, list=[A,B,C] -> return B, P=1<br>
     * P=1, list=[A,B,C] -> return A, P=0<br>
     * P=0, list=[A,B,C] -> return empty<br>
     * @return the undone action
     */
    public Optional<Action> undo() {
        if(this.pointerIndex == 0) {
            return Optional.empty();
        }
        return Optional.of(listEvents.get(--pointerIndex));
    }

    /**
     * Redo an action.<br>
     * P=0, list=[A]     -> return empty<br>
     * P=1, list=[A,B]   -> return empty<br>
     * P=0, list=[A,B]   -> return B, P=1<br>
     * P=2, list=[A,B,C] -> return empty<br>
     * P=1, list=[A,B,C] -> return C, P=2<br>
     * P=0, list=[A,B,C] -> return B, P=1<br>
     * @return the redone action
     */
    public Optional<Action> redo() {
        if(this.pointerIndex == this.listEvents.size()-1) {
            return Optional.empty();
        }
        return Optional.of(listEvents.get(++pointerIndex));
    }

    @Override
    public String toString() {
        List<String> collect = listEvents.stream()
                .map(Action::toString)
                .collect(Collectors.toList());
        for(int i = 0; i < collect.size(); i++) {
            collect.set(i, (i == pointerIndex ? "-> " : "   ") + collect.get(i));
        }
        final String ln = System.lineSeparator();
        return String.join(ln, collect) + ln + ln;
    }
}
