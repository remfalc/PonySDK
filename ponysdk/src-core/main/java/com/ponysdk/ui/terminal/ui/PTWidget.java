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

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.ponysdk.ui.terminal.DomHandlerType;
import com.ponysdk.ui.terminal.HandlerType;
import com.ponysdk.ui.terminal.Property;
import com.ponysdk.ui.terminal.PropertyKey;
import com.ponysdk.ui.terminal.UIService;
import com.ponysdk.ui.terminal.instruction.AddHandler;
import com.ponysdk.ui.terminal.instruction.Create;
import com.ponysdk.ui.terminal.instruction.EventInstruction;

public class PTWidget extends PTUIObject {

    @Override
    public void create(Create create, UIService uiService) {
        init(new com.google.gwt.user.client.ui.Widget());
    }

    @Override
    public com.google.gwt.user.client.ui.Widget cast() {
        return (com.google.gwt.user.client.ui.Widget) uiObject;
    }

    @Override
    public void addHandler(AddHandler addHandler, UIService uiService) {

        if (HandlerType.DOM_HANDLER.equals(addHandler.getType())) {
            final int domHandlerType = addHandler.getMainProperty().getIntValue();
            final com.google.gwt.user.client.ui.Widget w = asWidget(addHandler.getObjectID(), uiService);
            addDomHandler(addHandler, w, domHandlerType, uiService);
            return;
        } else {
            super.addHandler(addHandler, uiService);
        }

    }

    protected void triggerOnClick(final AddHandler addHandler, final com.google.gwt.user.client.ui.Widget widget, final int domHandlerType, final UIService uiService, ClickEvent event) {
        final EventInstruction eventInstruction = new EventInstruction(addHandler.getObjectID(), addHandler.getType());
        final Property main = new Property();
        main.setProperty(PropertyKey.DOM_HANDLER, domHandlerType);
        main.setProperty(PropertyKey.CLIENT_X, event.getClientX());
        main.setProperty(PropertyKey.CLIENT_Y, event.getClientY());
        main.setProperty(PropertyKey.SOURCE_ABSOLUTE_LEFT, widget.getAbsoluteLeft());
        main.setProperty(PropertyKey.SOURCE_ABSOLUTE_TOP, widget.getAbsoluteTop());
        main.setProperty(PropertyKey.SOURCE_OFFSET_HEIGHT, widget.getOffsetHeight());
        main.setProperty(PropertyKey.SOURCE_OFFSET_WIDTH, widget.getOffsetWidth());
        eventInstruction.setMainProperty(main);
        uiService.triggerEvent(eventInstruction);
    }

    protected void triggerOnMouseOver(final AddHandler addHandler, final int domHandlerType, final UIService uiService) {
        final EventInstruction eventInstruction = new EventInstruction(addHandler.getObjectID(), addHandler.getType());
        eventInstruction.getMainProperty().setProperty(PropertyKey.DOM_HANDLER, domHandlerType);
        uiService.triggerEvent(eventInstruction);
    }

    protected void triggerOnKeyPress(final AddHandler addHandler, final int domHandlerType, final UIService uiService, KeyPressEvent event) {
        final Property main = new Property(PropertyKey.VALUE, event.getNativeEvent().getKeyCode());
        main.setProperty(PropertyKey.DOM_HANDLER, domHandlerType);
        if (addHandler.getMainProperty().hasChildProperty(PropertyKey.KEY_FILTER)) {
            final List<Integer> keyCodes = addHandler.getMainProperty().getListIntegerProperty(PropertyKey.KEY_FILTER);
            if (keyCodes.contains(event.getNativeEvent().getKeyCode())) {
                final EventInstruction eventInstruction = new EventInstruction(addHandler.getObjectID(), addHandler.getType());
                eventInstruction.setMainProperty(main);
                uiService.triggerEvent(eventInstruction);
            }
        } else {
            final EventInstruction eventInstruction = new EventInstruction(addHandler.getObjectID(), addHandler.getType());
            eventInstruction.setMainProperty(main);
            uiService.triggerEvent(eventInstruction);
        }
    }

    private void addDomHandler(final AddHandler addHandler, final com.google.gwt.user.client.ui.Widget widget, final int domHandlerType, final UIService uiService) {

        final DomHandlerType h = DomHandlerType.values()[domHandlerType];
        switch (h) {
        case CLICK:
            widget.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    triggerOnClick(addHandler, widget, domHandlerType, uiService, event);
                }

            }, ClickEvent.getType());
            break;
        case MOUSE_OVER:
            widget.addDomHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    triggerOnMouseOver(addHandler, domHandlerType, uiService);
                }

            }, MouseOverEvent.getType());
            break;
        case KEY_PRESS:
            widget.addDomHandler(new KeyPressHandler() {
                @Override
                public void onKeyPress(KeyPressEvent event) {
                    triggerOnKeyPress(addHandler, domHandlerType, uiService, event);
                }

            }, KeyPressEvent.getType());
            break;
        case KEY_UP:

            if (widget instanceof TextBoxBase) {
                final TextBoxBase textBox = (TextBoxBase) widget;
                textBox.addKeyUpHandler(new KeyUpHandler() {

                    @Override
                    public void onKeyUp(KeyUpEvent event) {
                        final EventInstruction changeHandlerInstruction = new EventInstruction(addHandler.getObjectID(), HandlerType.STRING_VALUE_CHANGE_HANDLER);
                        changeHandlerInstruction.setMainPropertyValue(PropertyKey.VALUE, textBox.getText());
                        final EventInstruction eventInstruction = new EventInstruction(addHandler.getObjectID(), addHandler.getType());
                        final Property main = new Property(PropertyKey.VALUE, event.getNativeEvent().getKeyCode());
                        main.setProperty(PropertyKey.DOM_HANDLER, domHandlerType);
                        eventInstruction.setMainProperty(main);
                        if (addHandler.getMainProperty().hasChildProperty(PropertyKey.KEY_FILTER)) {
                            final List<Integer> keyCodes = addHandler.getMainProperty().getListIntegerProperty(PropertyKey.KEY_FILTER);
                            if (keyCodes.contains(event.getNativeEvent().getKeyCode())) {
                                uiService.triggerEvent(changeHandlerInstruction);
                                uiService.triggerEvent(eventInstruction);
                            }
                        } else {
                            uiService.triggerEvent(changeHandlerInstruction);
                            uiService.triggerEvent(eventInstruction);
                        }
                    }
                });
            } else {
                widget.addDomHandler(new KeyUpHandler() {
                    @Override
                    public void onKeyUp(KeyUpEvent event) {
                        final EventInstruction eventInstruction = new EventInstruction(addHandler.getObjectID(), addHandler.getType());
                        final Property main = new Property(PropertyKey.VALUE, event.getNativeEvent().getKeyCode());
                        main.setProperty(PropertyKey.DOM_HANDLER, domHandlerType);
                        eventInstruction.setMainProperty(main);
                        if (addHandler.getMainProperty().hasChildProperty(PropertyKey.KEY_FILTER)) {
                            final List<Integer> keyCodes = addHandler.getMainProperty().getListIntegerProperty(PropertyKey.KEY_FILTER);
                            if (keyCodes.contains(event.getNativeEvent().getKeyCode())) {
                                uiService.triggerEvent(eventInstruction);
                            }
                        } else {
                            uiService.triggerEvent(eventInstruction);
                        }
                    }
                }, KeyUpEvent.getType());
            }
            break;
        default:
            GWT.log("Handler not supported #" + h);
            break;
        }
    }

}