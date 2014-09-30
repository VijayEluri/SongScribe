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

    Created on Oct 1, 2006
*/
package songscribe.music;

/**
 * @author Csaba Kávai
 */
public abstract class NotNote extends Note {
    protected NotNote() {
    }

    protected NotNote(Note note) {
        super(note);
    }

    public int getYPos() {
        return 0;
    }

    public int getDotted() {
        return 0;
    }

    public Note.Accidental getAccidental() {
        return Note.Accidental.NONE;
    }

    public ForceArticulation getForceArticulation() {
        return null;
    }

    public DurationArticulation getDurationArticulation() {
        return null;
    }

    public int getDefaultDuration() {
        return 0;
    }
}
