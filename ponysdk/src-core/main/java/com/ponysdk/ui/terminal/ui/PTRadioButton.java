/*
 * Copyright (c) 2011 PonySDK
 *  Owners:
 *  Luciano Broussal  <luciano.broussal AT gmail.com>
 *	Mathieu Barbier   <mathieu.barbier AT gmail.com>
 *	Nicolas Ciaravola <nicolas.ciaravola.pro AT gmail.com>
 *  
 *  WebSite:
 *  http://code.google.com/p/pony-sdk/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ponysdk.ui.terminal.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.RadioButton;
import com.ponysdk.ui.terminal.Dictionnary.HANDLER;
import com.ponysdk.ui.terminal.Dictionnary.PROPERTY;
import com.ponysdk.ui.terminal.Dictionnary.TYPE;
import com.ponysdk.ui.terminal.UIService;
import com.ponysdk.ui.terminal.instruction.PTInstruction;

public class PTRadioButton extends PTCheckBox {

    private static Map<String, PTRadioButton> lastSelectedRadioButtonByGroup = new HashMap<String, PTRadioButton>();

    @Override
    public void create(final PTInstruction create, final UIService uiService) {
        init(create, uiService, new RadioButton(null));
    }

    @Override
    public void update(final PTInstruction update, final UIService uiService) {

        if (update.containsKey(PROPERTY.NAME)) {
            cast().setName(update.getString(PROPERTY.NAME));
        } else {
            super.update(update, uiService);
        }
    }

    @Override
    protected void addValueChangeHandler(final PTInstruction addHandler, final UIService uiService) {
        final RadioButton radioButton = cast();

        radioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(final ValueChangeEvent<Boolean> event) {
                fireInstruction(addHandler.getObjectID(), uiService, event.getValue());

                if (cast().getName() != null) {
                    final PTRadioButton previouslySelected = lastSelectedRadioButtonByGroup.get(cast().getName());
                    if (previouslySelected != null && !previouslySelected.equals(radioButton)) {
                        fireInstruction(previouslySelected.getObjectID(), uiService, previouslySelected.cast().getValue());
                    }
                    lastSelectedRadioButtonByGroup.put(radioButton.getName(), PTRadioButton.this);
                }
            }
        });
    }

    protected void fireInstruction(final long objectID, final UIService uiService, final boolean value) {
        final PTInstruction eventInstruction = new PTInstruction();
        eventInstruction.setObjectID(objectID);
        eventInstruction.put(TYPE.KEY, TYPE.KEY_.EVENT);
        eventInstruction.put(HANDLER.KEY, HANDLER.KEY_.BOOLEAN_VALUE_CHANGE_HANDLER);
        eventInstruction.put(PROPERTY.VALUE, value);
        uiService.triggerEvent(eventInstruction);
    }

    @Override
    public RadioButton cast() {
        return (RadioButton) uiObject;
    }

}
