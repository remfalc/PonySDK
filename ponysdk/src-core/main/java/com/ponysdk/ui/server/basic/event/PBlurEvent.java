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

package com.ponysdk.ui.server.basic.event;

import com.ponysdk.ui.terminal.DomHandlerType;

public class PBlurEvent extends PMouseEvent<PBlurHandler> {

    public static final PDomEvent.Type<PBlurHandler> TYPE = new PDomEvent.Type<PBlurHandler>(DomHandlerType.BLUR);

    public PBlurEvent(final Object sourceComponent) {
        super(sourceComponent);
    }

    @Override
    public PDomEvent.Type<PBlurHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final PBlurHandler handler) {
        handler.onBlur(this);
    }

}