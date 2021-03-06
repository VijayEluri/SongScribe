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

    Created on Jul 22, 2006
*/
package songscribe.ui.adjustment;

import songscribe.data.*;
import songscribe.music.Annotation;
import songscribe.music.Composition;
import songscribe.music.Line;
import songscribe.music.Note;
import songscribe.ui.MusicSheet;

import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * @author Csaba Kávai
 */
public class VerticalAdjustment extends Adjustment {
    private AdjustRect draggingRect;
    private ArrayList<AdjustRect> adjustRects = new ArrayList<AdjustRect>();

    public VerticalAdjustment(MusicSheet parent) {
        super(parent);
    }

    protected void startedDrag() {
        draggingRect = null;

        for (AdjustRect ar : adjustRects) {
            if (ar.rectangle.contains(startPoint)) {
                draggingRect = ar;
                break;
            }
        }

        if (draggingRect == null) {
            startedDrag = false;
        }
        else {
            if (draggingRect.adjustType == AdjustType.RIGHT_INFO) {
                upLeftDragBounds.setLocation(draggingRect.rectangle.x, 0);
                downRightDragBounds.setLocation(draggingRect.rectangle.x, Integer.MAX_VALUE);
            }
            else if (draggingRect.adjustType == AdjustType.TOP_SPACE) {
                upLeftDragBounds.setLocation(draggingRect.rectangle.x, 0);
                downRightDragBounds.setLocation(draggingRect.rectangle.x, Integer.MAX_VALUE);
            }
            else if (draggingRect.adjustType == AdjustType.ROW_HEIGHT) {
                upLeftDragBounds.setLocation(draggingRect.rectangle.x, musicSheet.getNoteYPos(6, 0));
                downRightDragBounds.setLocation(draggingRect.rectangle.x, Integer.MAX_VALUE);
            }
            else if (draggingRect.adjustType == AdjustType.TEMPO_CHANGE ||
                     draggingRect.adjustType == AdjustType.FSENDING || draggingRect.adjustType == AdjustType.TRILL ||
                     draggingRect.adjustType == AdjustType.BEAT_CHANGE) {
                upLeftDragBounds.setLocation(draggingRect.rectangle.x, musicSheet.getNoteYPos(6,
                        draggingRect.line - 1));
                downRightDragBounds.setLocation(draggingRect.rectangle.x, musicSheet.getNoteYPos(-4, draggingRect.line));
            }
            else if (draggingRect.adjustType == AdjustType.ANNOTATION) {
                upLeftDragBounds.setLocation(draggingRect.rectangle.x, musicSheet.getNoteYPos(6,
                        draggingRect.line - 1));
                downRightDragBounds.setLocation(draggingRect.rectangle.x, musicSheet.getNoteYPos(-6,
                        draggingRect.line + 1));
            }
            else if (draggingRect.adjustType == AdjustType.SLUR_CTRL_Y || draggingRect.adjustType == AdjustType.TUPLET ||
                     draggingRect.adjustType == AdjustType.CRESCENDO_Y ||
                     draggingRect.adjustType == AdjustType.DIMINUENDO_Y) {
                upLeftDragBounds.setLocation(draggingRect.rectangle.x, musicSheet.getNoteYPos(6, draggingRect.line - 1));
                downRightDragBounds.setLocation(draggingRect.rectangle.x, musicSheet.getNoteYPos(-6, draggingRect.line + 1));
            }
            else {
                upLeftDragBounds.setLocation(0, 0);
                downRightDragBounds.setLocation(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
        }
    }

    protected void drag() {
        if (draggingRect == null) {
            return;
        }

        int diffX = endPoint.x - draggingRect.rectangle.x + draggingRect.rectangle.width / 2;
        int diffY = endPoint.y - draggingRect.rectangle.y + draggingRect.rectangle.height / 2;
        draggingRect.rectangle.y = endPoint.y - draggingRect.rectangle.height / 2;

        if (draggingRect.adjustType == AdjustType.RIGHT_INFO) {
            musicSheet.getComposition().setRightInfoStartY(musicSheet.getComposition().getRightInfoStartY() + diffY);
        }
        else if (draggingRect.adjustType == AdjustType.TOP_SPACE) {
            musicSheet.getComposition().setTopSpace(musicSheet.getComposition().getTopSpace() + diffY, true);
        }
        else if (draggingRect.adjustType == AdjustType.ROW_HEIGHT) {
            musicSheet.getComposition().setRowHeight(musicSheet.getComposition().getRowHeight() + diffY);
        }
        else if (draggingRect.adjustType == AdjustType.TEMPO_CHANGE) {
            Line line = musicSheet.getComposition().getLine(draggingRect.line);
            line.setTempoChangeYPos(line.getTempoChangeYPos() + diffY);
        }
        else if (draggingRect.adjustType == AdjustType.BEAT_CHANGE) {
            Line line = musicSheet.getComposition().getLine(draggingRect.line);
            line.setBeatChangeYPos(line.getBeatChangeYPos() + diffY);
        }
        else if (draggingRect.adjustType == AdjustType.FSENDING) {
            Line line = musicSheet.getComposition().getLine(draggingRect.line);
            line.setFsEndingYPos(line.getFsEndingYPos() + diffY);
        }
        else if (draggingRect.adjustType == AdjustType.ANNOTATION) {
            Annotation a = musicSheet.getComposition().getLine(draggingRect.line).getNote(draggingRect.xIndex).getAnnotation();
            a.setYPos(a.getYPos() + diffY);
        }
        else if (draggingRect.adjustType == AdjustType.TRILL) {
            Line line = musicSheet.getComposition().getLine(draggingRect.line);
            line.setTrillYPos(line.getTrillYPos() + diffY);
        }
        else if (draggingRect.adjustType == AdjustType.CRESCENDO_Y ||
                 draggingRect.adjustType == AdjustType.DIMINUENDO_Y) {
            Line line = musicSheet.getComposition().getLine(draggingRect.line);
            Interval interval = getCresDecrIntervalSet(line, draggingRect.adjustType).findInterval(draggingRect.xIndex);
            CrescendoDiminuendoIntervalData.setYShift(interval,
                    CrescendoDiminuendoIntervalData.getYShift(interval) + diffY);
        }
        else if (draggingRect.adjustType == AdjustType.SLUR_POS1 || draggingRect.adjustType == AdjustType.SLUR_POS2 ||
                 draggingRect.adjustType == AdjustType.SLUR_CTRL_Y) {
            Line line = musicSheet.getComposition().getLine(draggingRect.line);
            Interval interval = line.getSlurs().findInterval(draggingRect.xIndex);
            SlurData slurData = new SlurData(interval.getData());

            switch (draggingRect.adjustType) {
                case SLUR_POS1:
                    draggingRect.rectangle.x = endPoint.x - draggingRect.rectangle.width / 2;
                    slurData.setXPos1(slurData.getXPos1() + diffX);
                    slurData.setYPos1(slurData.getYPos1() + diffY);
                    break;

                case SLUR_POS2:
                    draggingRect.rectangle.x = endPoint.x - draggingRect.rectangle.width / 2;
                    slurData.setXPos2(slurData.getXPos2() + diffX);
                    slurData.setYPos2(slurData.getYPos2() + diffY);
                    break;

                case SLUR_CTRL_Y:
                    slurData.setCtrlY(slurData.getCtrlY() + diffY);
            }

            interval.setData(slurData.toString());
        }
        else if (draggingRect.adjustType == AdjustType.TUPLET) {
            Interval interval = musicSheet.getComposition().getLine(draggingRect.line).getTuplets().findInterval(draggingRect.xIndex);
            TupletIntervalData.setVerticalPosition(interval, TupletIntervalData.getVerticalPosition(interval) + diffY);
        }

        musicSheet.viewChanged();
        musicSheet.getComposition().modifiedComposition();
        revalidateRects();
        musicSheet.setRepaintImage(true);
        musicSheet.repaint();
    }

    protected void finishedDrag() {
        draggingRect = null;
    }

    public void repaint(Graphics2D g2) {
        for (AdjustRect ar : adjustRects) {
            g2.setPaint(ar.adjustType.getColor());
            g2.fill(ar.rectangle);
            g2.setPaint(Color.black);
            g2.draw(ar.rectangle);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            Composition c = musicSheet.getComposition();

            if (c.getRightInfo().length() > 0) {
                adjustRects.add(new AdjustRect(-1, AdjustType.RIGHT_INFO, -1));
            }

            if (c.lineCount() > 0) {
                adjustRects.add(new AdjustRect(0, AdjustType.TOP_SPACE, -1));
            }

            if (c.lineCount() > 1) {
                adjustRects.add(new AdjustRect(1, AdjustType.ROW_HEIGHT, -1));
            }

            for (int l = 0; l < c.lineCount(); l++) {
                Line line = c.getLine(l);

                int firstTempoChange = line.getFirstTempoChange();

                if (firstTempoChange > -1) {
                    adjustRects.add(new AdjustRect(l, AdjustType.TEMPO_CHANGE, firstTempoChange));
                }

                for (int n = 0; n < line.noteCount(); n++) {
                    if (line.getNote(n).getAnnotation() != null) {
                        adjustRects.add(new AdjustRect(l, AdjustType.ANNOTATION, n));
                    }
                }

                if (!line.getFsEndings().isEmpty()) {
                    adjustRects.add(new AdjustRect(l, AdjustType.FSENDING, line.getFsEndings().listIterator().next().getA()));
                }

                int firstTrill = line.getFirstTrill();

                if (firstTrill > -1) {
                    adjustRects.add(new AdjustRect(l, AdjustType.TRILL, firstTrill));
                }

                int firstBeatChange = line.getFirstBeatChange();

                if (firstBeatChange > -1) {
                    adjustRects.add(new AdjustRect(l, AdjustType.BEAT_CHANGE, firstBeatChange));
                }

                for (ListIterator<Interval> li = line.getSlurs().listIterator(); li.hasNext(); ) {
                    Interval interval = li.next();
                    adjustRects.add(new AdjustRect(l, AdjustType.SLUR_POS1, interval.getA()));
                    adjustRects.add(new AdjustRect(l, AdjustType.SLUR_POS2, interval.getB()));
                    adjustRects.add(new AdjustRect(l, AdjustType.SLUR_CTRL_Y, interval.getA()));
                }

                for (ListIterator<Interval> li = line.getCrescendo().listIterator(); li.hasNext(); ) {
                    Interval interval = li.next();
                    adjustRects.add(new AdjustRect(l, AdjustType.CRESCENDO_Y, interval.getA()));
                }

                for (ListIterator<Interval> li = line.getDiminuendo().listIterator(); li.hasNext(); ) {
                    Interval interval = li.next();
                    adjustRects.add(new AdjustRect(l, AdjustType.DIMINUENDO_Y, interval.getA()));
                }

                for (ListIterator<Interval> li = line.getTuplets().listIterator(); li.hasNext(); ) {
                    Interval interval = li.next();
                    adjustRects.add(new AdjustRect(l, AdjustType.TUPLET, interval.getA()));
                }
            }

        }
        else {
            adjustRects.clear();
        }
    }

    private void getRectangle(AdjustRect ar) {
        if (ar.adjustType == AdjustType.RIGHT_INFO) {
            ar.rectangle.x = musicSheet.getSheetWidth() - 8;
            ar.rectangle.y = musicSheet.getComposition().getRightInfoStartY();
        }
        else if (ar.adjustType == AdjustType.TOP_SPACE || ar.adjustType == AdjustType.ROW_HEIGHT) {
            ar.rectangle.x = 0;
            ar.rectangle.y = musicSheet.getNoteYPos(0, ar.line) - 4;
        }
        else if (ar.adjustType == AdjustType.TEMPO_CHANGE) {
            ar.rectangle.x = musicSheet.getComposition().getLine(ar.line).getNote(ar.xIndex).getXPos() - 8;
            ar.rectangle.y = musicSheet.getNoteYPos(0, ar.line) +
                             musicSheet.getComposition().getLine(ar.line).getTempoChangeYPos() - 8;
        }
        else if (ar.adjustType == AdjustType.BEAT_CHANGE) {
            ar.rectangle.x = musicSheet.getComposition().getLine(ar.line).getNote(ar.xIndex).getXPos() - 8;
            ar.rectangle.y = musicSheet.getNoteYPos(0, ar.line) +
                             musicSheet.getComposition().getLine(ar.line).getBeatChangeYPos() - 8;
        }
        else if (ar.adjustType == AdjustType.FSENDING) {
            ar.rectangle.x = musicSheet.getComposition().getLine(ar.line).getNote(ar.xIndex).getXPos() - 12;
            ar.rectangle.y = musicSheet.getNoteYPos(0, ar.line) +
                             musicSheet.getComposition().getLine(ar.line).getFsEndingYPos() - 8;
        }
        else if (ar.adjustType == AdjustType.ANNOTATION) {
            Note note = musicSheet.getComposition().getLine(ar.line).getNote(ar.xIndex);
            ar.rectangle.x =
                    (int) Math.round(musicSheet.getDrawer().getAnnotationXPos((Graphics2D) musicSheet.getGraphics(), note)) -
                    8;
            ar.rectangle.y = musicSheet.getDrawer().getAnnotationYPos(ar.line, note) - 8;
        }
        else if (ar.adjustType == AdjustType.TRILL) {
            ar.rectangle.x = musicSheet.getComposition().getLine(ar.line).getNote(ar.xIndex).getXPos() - 12;
            ar.rectangle.y =
                    musicSheet.getNoteYPos(0, ar.line) + musicSheet.getComposition().getLine(ar.line).getTrillYPos() -
                    8;
        }
        else if (ar.adjustType == AdjustType.CRESCENDO_Y || ar.adjustType == AdjustType.DIMINUENDO_Y) {
            Line line = musicSheet.getComposition().getLine(ar.line);
            Interval interval = getCresDecrIntervalSet(line, ar.adjustType).findInterval(ar.xIndex);
            ar.rectangle.x =
                    (line.getNote(interval.getA()).getXPos() + line.getNote(interval.getB()).getXPos() + 12) / 2;
            ar.rectangle.y =
                    musicSheet.getNoteYPos(6, ar.line) - 4 + CrescendoDiminuendoIntervalData.getYShift(interval);
        }
        else if (ar.adjustType == AdjustType.SLUR_POS1 || ar.adjustType == AdjustType.SLUR_POS2 ||
                 ar.adjustType == AdjustType.SLUR_CTRL_Y) {
            Line line = musicSheet.getComposition().getLine(ar.line);
            Interval interval = line.getSlurs().findInterval(ar.xIndex);
            SlurData slurData = new SlurData(interval.getData());
            Note note = line.getNote(ar.xIndex);

            switch (ar.adjustType) {
                case SLUR_POS1:
                    ar.rectangle.x = note.getXPos() + slurData.getXPos1();
                    ar.rectangle.y = musicSheet.getNoteYPos(note.getYPos(), ar.line) + slurData.getYPos1();
                    ar.rectangle.y += slurData.getCtrlY() > 0 ? 8 : -20;
                    break;

                case SLUR_POS2:
                    ar.rectangle.x = note.getXPos() + slurData.getXPos2();
                    ar.rectangle.y = musicSheet.getNoteYPos(note.getYPos(), ar.line) + slurData.getYPos2();
                    ar.rectangle.y += slurData.getCtrlY() > 0 ? 8 : -20;
                    break;

                case SLUR_CTRL_Y:
                    Note lastNote = line.getNote(interval.getB());
                    ar.rectangle.x =
                            (note.getXPos() + slurData.getXPos1() + lastNote.getXPos() + slurData.getXPos2()) / 2;
                    ar.rectangle.y = (musicSheet.getNoteYPos(note.getYPos(), ar.line) +
                                      musicSheet.getNoteYPos(lastNote.getYPos(), ar.line)
                                     ) / 2 + slurData.getCtrlY();
                    ar.rectangle.y -= 4;
                    break;
            }

            ar.rectangle.x -= 4;
        }
        else if (ar.adjustType == AdjustType.TUPLET) {
            Line line = musicSheet.getComposition().getLine(ar.line);
            Interval interval = line.getTuplets().findInterval(ar.xIndex);
            Note note = line.getNote(interval.getA());
            ar.rectangle.x = note.getXPos() + (note.isUpper() ? 0 : -10);
            ar.rectangle.y = musicSheet.getNoteYPos(note.getYPos() + (note.isUpper() ? -10 : -3), ar.line) +
                             TupletIntervalData.getVerticalPosition(interval);
        }

        ar.rectangle.width = ar.rectangle.height = 8;
    }

    private void revalidateRects() {
        for (AdjustRect ar : adjustRects) {
            getRectangle(ar);
        }
    }

    private IntervalSet getCresDecrIntervalSet(Line line, AdjustType adjustType) {
        switch (adjustType) {
            case CRESCENDO_Y:
                return line.getCrescendo();

            case DIMINUENDO_Y:
                return line.getDiminuendo();

            default:
                throw new IllegalArgumentException(String.valueOf(adjustType));
        }
    }

    private enum AdjustType {
        RIGHT_INFO(Color.blue),
        TOP_SPACE(Color.cyan),
        ROW_HEIGHT(Color.orange),
        TEMPO_CHANGE(Color.red),
        BEAT_CHANGE(Color.pink),
        FSENDING(Color.green),
        ANNOTATION(Color.magenta),
        TRILL(Color.pink),
        SLUR_POS1(Color.orange),
        SLUR_POS2(Color.orange),
        SLUR_CTRL_Y(Color.orange),
        CRESCENDO_Y(Color.green),
        DIMINUENDO_Y(Color.green),
        TUPLET(Color.pink);

        private Color color;

        AdjustType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    private class AdjustRect {
        Rectangle rectangle;
        int line, xIndex;
        AdjustType adjustType;

        public AdjustRect(int line, AdjustType adjustType, int xIndex) {
            this.line = line;
            this.adjustType = adjustType;
            this.xIndex = xIndex;
            rectangle = new Rectangle();
            getRectangle(this);
        }
    }
}
