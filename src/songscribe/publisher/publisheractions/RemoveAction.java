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
 
Created on Sep 26, 2006
*/
package songscribe.publisher.publisheractions;

import songscribe.publisher.Publisher;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

/**
 * @author Csaba Kávai
 */
public class RemoveAction extends AbstractAction{
    private Publisher publisher;

    public RemoveAction(Publisher publisher) {
        this.publisher = publisher;
        putValue(Action.NAME, "Delete");
        putValue(Action.SMALL_ICON, new ImageIcon(Publisher.getImage("editdelete.png")));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
    }

    public void actionPerformed(ActionEvent e) {
        if(publisher.isBookNull())return;
        publisher.getBook().removeSelected();
    }
}
