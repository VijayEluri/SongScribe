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

    Created on Sep 23, 2005
*/
package songscribe.music;

import songscribe.ui.MainFrame;

import java.awt.*;

/**
 * @author Csaba Kávai
 */
public class RepeatRight extends NotNote {
    public static final Image IMAGE = MainFrame.getImage("repeatRight.gif");
    public static final Rectangle REAL_NOTE_RECT = new Rectangle(5, 12, 13, 32);

    public RepeatRight() {
        super();
    }

    private RepeatRight(Note note) {
        super(note);
    }

    public RepeatRight clone() {
        return new RepeatRight(this);
    }

    public Image getUpImage() {
        return IMAGE;
    }

    public Image getDownImage() {
        return IMAGE;
    }

    public NoteType getNoteType() {
        return NoteType.REPEAT_RIGHT;
    }

    public Rectangle getRealUpNoteRect() {
        return REAL_NOTE_RECT;
    }

    public Rectangle getRealDownNoteRect() {
        return REAL_NOTE_RECT;
    }
}
