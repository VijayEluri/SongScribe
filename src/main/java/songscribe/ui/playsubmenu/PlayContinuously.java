/* 
SongScribe song notation program
Copyright (C) 2006-2007 Csaba Kavai

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

Created on Oct 8, 2006
*/
package songscribe.ui.playsubmenu;

import songscribe.ui.Constants;
import songscribe.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Csaba Kávai
 */
class PlayContinuously extends AbstractAction{
    private PlayMenu playMenu;

    public PlayContinuously(PlayMenu playMenu){
        this.playMenu = playMenu;
        putValue(NAME, "Play Continuously");
        putValue(SMALL_ICON, new ImageIcon(MainFrame.getImage("playcontinuously.png")));
    }

    public void actionPerformed(ActionEvent e) {
        playMenu.getMainFrame().getProperties().setProperty(Constants.PLAYCONTINUOUSLYPROP, ((AbstractButton)e.getSource()).isSelected() ? Constants.TRUEVALUE : Constants.FALSEVALUE);
        playMenu.getMainFrame().fireMusicChanged(((Component)e.getSource()).getParent());
    }
}
