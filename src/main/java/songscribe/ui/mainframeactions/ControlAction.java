/*
    SongScribe song notation program
    Copyright (C) 2006 Csaba Kavai

    This file is part of SongScribe.

    SongScribe is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    SongScribe is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Created on Aug 6, 2006
*/
package songscribe.ui.mainframeactions;

import songscribe.ui.MainFrame;
import songscribe.ui.MultiAccessibleAction;
import songscribe.ui.MusicSheet;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Csaba Kávai
 */
public class ControlAction extends MultiAccessibleAction {
    private MusicSheet.Control control;
    private MainFrame mainFrame;

    public ControlAction(MainFrame mainFrame, MusicSheet.Control control) {
        this.mainFrame = mainFrame;
        this.control = control;
        putValue(Action.NAME, control.getDescription());
        putValue(Action.SMALL_ICON, new ImageIcon(MainFrame.getImage(control.name().toLowerCase() + ".png")));
    }

    public void actionPerformed(ActionEvent e) {
        mainFrame.getMusicSheet().setControl(control);
        super.actionPerformed(e);
        mainFrame.getStatusBar().setControlLabel(control.getDescription());
    }
}
