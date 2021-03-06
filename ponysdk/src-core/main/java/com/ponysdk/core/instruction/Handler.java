
package com.ponysdk.core.instruction;

import com.ponysdk.ui.terminal.Dictionnary.HANDLER;

public abstract class Handler extends Instruction {

    public Handler(final long objectID, final String type) {
        super(objectID);
        put(HANDLER.KEY, type);
    }

    public String getType() {
        return getString(HANDLER.KEY);
    }

    public boolean isTypeOf(final String type) {
        return getType().equals(type);
    }

}
