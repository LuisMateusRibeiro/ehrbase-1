package org.ehrbase.aql.sql.binding;

import java.util.List;
import org.ehrbase.aql.definition.VariableDefinition;

/**
 * check if a where variable represents a temporal object. This is used to apply proper type casting and
 * relevant operator using EPOCH_OFFSET instead of string value when dealing with date/time comparison in
 * json structure
 */
public class WhereTemporal {
    List<Object> whereItems;

    public WhereTemporal(List<Object> whereItems) {
        this.whereItems = whereItems;
    }

    public boolean containsTemporalItem(VariableDefinition variableDefinition) {

        // get the index of variable definition in item list
        int pos = whereItems.indexOf(variableDefinition);

        for (Object item : whereItems.subList(pos, whereItems.size())) {

            if (item instanceof String
                    && new DateTimes((String) item).isDateTimeZoned()) { // ignore variable definition
                return true;
            }
        }
        return false;
    }
}
