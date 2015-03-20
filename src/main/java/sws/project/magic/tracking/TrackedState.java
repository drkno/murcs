package sws.project.magic.tracking;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Tracks the current value of an object.
 */
public class TrackedState {

    private ArrayList<FieldValuePair> fieldValuePairs;

    /**
     * Instantiates a new tracked object.
     * @param initialObject The object to track.
     * @param fields Fields to track.
     * @throws Exception Exception if retrieving values failed due to unknown type.
     */
    public TrackedState(Object initialObject, ArrayList<Field> fields) throws Exception {
        fieldValuePairs = new ArrayList<>();
        for (Field field : fields) {
            fieldValuePairs.add(new FieldValuePair(initialObject, field));
        }
    }

    /**
     * Determines the difference between the current object representation and the stored representation.
     * @param obj Object to get values from
     * @param description Description of the change
     * @param initialSave Save all fields rather than just changed.
     * @return A representation of the difference between these states
     * @throws Exception If retrieving the values of the current object state is not possible.
     */
    public ValueChange difference(Object obj, String description, boolean initialSave) throws Exception {
        ArrayList<FieldValuePair> changedFieldValuePairs = new ArrayList<>();
        for (int i = 0; i < fieldValuePairs.size(); i++) {
            FieldValuePair pair = fieldValuePairs.get(i);
            Field field = pair.getField();
            Object value = FieldValuePair.getValueFromObject(obj, field);

            if (initialSave || value == null && pair.getValue() == null || !value.equals(pair.getValue())) {
                FieldValuePair newPair = new FieldValuePair(field, value);
                changedFieldValuePairs.add(newPair);
                fieldValuePairs.set(i, newPair);
            }
        }

        FieldValuePair[] pairs = new FieldValuePair[changedFieldValuePairs.size()];
        changedFieldValuePairs.toArray(pairs);
        return new ValueChange(obj, pairs, description, this);
    }

    /**
     * Determines what the previous state was if it was lost.
     * @param original The object that the original value applied to.
     * @param previous The change before the previous state.
     * @param description The description of the change.
     * @return A representation of the previous state.
     * @throws Exception if object specified is of a different type to the previous object.
     */
    public ValueChange dumpChange(Object original, ValueChange previous, String description) throws Exception {
        FieldValuePair[] previousFieldValuePairs = previous.getChangedFields();
        ArrayList<FieldValuePair> changedFieldValuePairs = new ArrayList<>();

        for (int i = 0; i < previousFieldValuePairs.length; i++) {
            int j;
            for (j = 0; j < fieldValuePairs.size(); j++) {
                if (fieldValuePairs.get(j).getField().equals(previousFieldValuePairs[i].getField())) {
                    break;
                }
            }
            if (j >= fieldValuePairs.size()) {
                throw new Exception("Field should exist. Tracker in an inconsistent state.");
            }
            if (previousFieldValuePairs[i].getValue() == null && fieldValuePairs.get(j).getValue() == null ||
                    !previousFieldValuePairs[i].getValue().equals(fieldValuePairs.get(j).getValue())) {
                changedFieldValuePairs.add(new FieldValuePair(previousFieldValuePairs[i].getField(), fieldValuePairs.get(j).getValue()));
            }
        }

        previousFieldValuePairs = new FieldValuePair[changedFieldValuePairs.size()];
        changedFieldValuePairs.toArray(previousFieldValuePairs);
        return new ValueChange(original, previousFieldValuePairs, description, this);
    }

    /**
     * Applies the changes in a TrackedChange to this historical view of the object.
     * @param state new state of the object.
     */
    public void apply(ValueChange state) {
        FieldValuePair[] fields = state.getChangedFields();
        for (FieldValuePair field : fields) {

            int index = fieldValuePairs.indexOf(field);
            if (index >= 0) {
                fieldValuePairs.get(index).setValue(field.getValue());
            } else {
                System.err.println("If you are seeing this message you did something really wrong...\n" +
                        "Could not update tracker history - this will only have affect" +
                        " if you attempt to do (not redo) after undoing.");
            }
        }
    }
}
