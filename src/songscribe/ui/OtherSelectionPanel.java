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

Created on Sep 23, 2005
*/
package songscribe.ui;

import songscribe.music.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.*;

/**
 * @author Csaba Kávai
 */
public class OtherSelectionPanel extends SelectionPanel implements ActionListener{
    public OtherSelectionPanel(MainFrame mainFrame) {
        super(mainFrame);
        NoteType[] noteTypes = new NoteType[]{NoteType.REPEATLEFT, NoteType.REPEATRIGHT, NoteType.BREATHMARK, NoteType.SINGLEBARLINE, NoteType.DOUBLEBARLINE, NoteType.FINALDOUBLEBARLINE};
        for (NoteType nt : noteTypes) {
            JToggleButton otherButton = new JToggleButton();
            otherButton.setToolTipText(nt.getCompoundName());
            otherButton.setIcon(new ImageIcon(Note.clipNoteImage(nt.getInstance().getUpImage(), nt.getInstance().getRealUpNoteRect(), Color.yellow, SELECTIONIMAGEDIM)));
            otherButton.addActionListener(this);
            otherButton.setActionCommand(nt.name());
            addSelectionComponent(otherButton);
            selectionGroup.add(otherButton);
        }
        ((AbstractButton)getComponent(0)).setSelected(true);
    }
}
