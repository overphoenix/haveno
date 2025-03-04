/*
 * This file is part of Haveno.
 *
 * Haveno is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Haveno is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Haveno. If not, see <http://www.gnu.org/licenses/>.
 */

package haveno.desktop.main.overlays.windows;

import haveno.core.alert.Alert;
import haveno.core.locale.Res;
import haveno.desktop.components.HyperlinkWithIcon;
import haveno.desktop.main.overlays.Overlay;
import haveno.desktop.util.FormBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static haveno.desktop.util.FormBuilder.addMultilineLabel;

public class DisplayAlertMessageWindow extends Overlay<DisplayAlertMessageWindow> {
    private static final Logger log = LoggerFactory.getLogger(DisplayAlertMessageWindow.class);
    private Alert alert;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Public API
    ///////////////////////////////////////////////////////////////////////////////////////////

    public DisplayAlertMessageWindow() {
        type = Type.Attention;
    }

    public void show() {
        width = 768;
        // need to set headLine, otherwise the fields will not be created in addHeadLine
        createGridPane();

        checkNotNull(alert, "alertMessage must not be null");

        if (alert.isSoftwareUpdateNotification()) {
            information("");
            headLine = Res.get("displayAlertMessageWindow.update.headline");
        } else {
            error("");
            headLine = Res.get("displayAlertMessageWindow.headline");
        }

        headLine = Res.get("displayAlertMessageWindow.headline");
        addHeadLine();
        addContent();
        addButtons();
        applyStyles();
        display();
    }

    public DisplayAlertMessageWindow alertMessage(Alert alert) {
        this.alert = alert;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void addContent() {
        checkNotNull(alert, "alertMessage must not be null");
        addMultilineLabel(gridPane, ++rowIndex, alert.getMessage(), 10);
        if (alert.isSoftwareUpdateNotification()) {
            String url = "https://haveno.exchange/downloads";
            HyperlinkWithIcon hyperlinkWithIcon = FormBuilder.addLabelHyperlinkWithIcon(gridPane, ++rowIndex,
                    Res.get("displayAlertMessageWindow.update.download"), url, url).second;
            hyperlinkWithIcon.setMaxWidth(550);
        }
    }


}
