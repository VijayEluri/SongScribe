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

    Created on 2006.01.01.
*/
package songscribe.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author Csaba Kávai
 */
public class PreferencesDialog extends MyDialog {
    // playback
    private JSlider durationShortenSlider = new JSlider(34, 100);
    private JCheckBox playInsertingNoteCheck = new JCheckBox("Play the tone when adding a note");
    private JCheckBox colorizeNoteCheck = new JCheckBox("<html>Colorize the currently played note<br>when playing the song back.<html>");


    public PreferencesDialog(MainFrame mainFrame) {
        super(mainFrame, "Preferences");

        Dimension small = new Dimension(0, 5);
        Dimension large = new Dimension(0, 15);
        JComponent c;

        // playback
        JPanel playbackPanel = new JPanel();
        playbackPanel.setLayout(new BoxLayout(playbackPanel, BoxLayout.Y_AXIS));
        playbackPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        c = new JLabel("Note duration fill:");
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        playbackPanel.add(c);
        playbackPanel.add(Box.createRigidArea(small));
        durationShortenSlider.setMajorTickSpacing(33);
        durationShortenSlider.setMinorTickSpacing(11);
        durationShortenSlider.setSnapToTicks(true);
        durationShortenSlider.setPaintLabels(true);
        durationShortenSlider.setPaintTicks(true);
        Dictionary<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
        labels.put(34, new JLabel("Staccato"));
        labels.put(67, new JLabel("Normal"));
        labels.put(100, new JLabel("Legato"));
        durationShortenSlider.setLabelTable(labels);
        durationShortenSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        playbackPanel.add(durationShortenSlider);
        playbackPanel.add(Box.createRigidArea(large));
        playbackPanel.add(playInsertingNoteCheck);
        playbackPanel.add(Box.createRigidArea(large));
        playbackPanel.add(colorizeNoteCheck);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Playback", playbackPanel);

        dialogPanel.add(BorderLayout.CENTER, tabbedPane);
        dialogPanel.add(BorderLayout.SOUTH, southPanel);
    }

    protected void getData() {
        Properties props = mainFrame.getProperties();
        durationShortenSlider.setValue(Integer.parseInt(props.getProperty(Constants.DURATION_SHORTEN_PROP)));
        playInsertingNoteCheck.setSelected(props.getProperty(Constants.PLAY_INSERTING_NOTE).equals(Constants.TRUE_VALUE));
        colorizeNoteCheck.setSelected(props.getProperty(Constants.COLORIZE_NOTE).equals(Constants.TRUE_VALUE));
    }

    protected void setData() {
        Properties props = mainFrame.getProperties();
        props.setProperty(Constants.DURATION_SHORTEN_PROP, Integer.toString(durationShortenSlider.getValue()));
        props.setProperty(Constants.PLAY_INSERTING_NOTE, playInsertingNoteCheck.isSelected() ? Constants.TRUE_VALUE : Constants.FALSE_VALUE);
        props.setProperty(Constants.COLORIZE_NOTE, colorizeNoteCheck.isSelected() ? Constants.TRUE_VALUE : Constants.FALSE_VALUE);
        mainFrame.fireMusicChanged(this);
    }
}
