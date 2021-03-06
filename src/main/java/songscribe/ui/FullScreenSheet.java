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

    Created on: 2006.03.05.
*/
package songscribe.ui;

import songscribe.data.MyBorder;
import songscribe.music.Line;
import songscribe.music.NoteType;
import songscribe.ui.playsubmenu.PlaybackListener;

import com.bulenkov.iconloader.JBHiDPIScaledImage;
import com.bulenkov.iconloader.util.UIUtil;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * @author Csaba Kávai
 */
public class FullScreenSheet extends JFrame implements MetaEventListener, PlaybackListener {
    private static final double MAX_ZOOM_RATIO = 2d;
    private static final int MARGIN = 50;
    private static BufferedImage zoomedImage;
    private MainFrame mainFrame;
    private JSlider tempoChangeSlider;
    private JButton playPauseButton;
    private MusicSheetComponent musicSheetComponent = new MusicSheetComponent();
    private double zoom;
    private int zoomedImageWidth;
    private int zoomedImageHeight;
    private Dimension sheetSize;

    public FullScreenSheet(MainFrame mainFrame, ChangeListener sliderChangeListener, Action... actions) throws HeadlessException {
        this.mainFrame = mainFrame;
        setUndecorated(true);
        getContentPane().add(BorderLayout.CENTER, musicSheetComponent);
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        init(sliderChangeListener, actions);
        setMusicSheet();

        if (MainFrame.sequencer != null) {
            MainFrame.sequencer.removeMetaEventListener(mainFrame.getMusicSheet());
            MainFrame.sequencer.addMetaEventListener(this);
        }

        mainFrame.getPlayMenu().addPlaybackListener(this);
        musicSheetComponent.requestFocusInWindow();
    }

    private void init(ChangeListener sliderChangeListener, Action... actions) {
        Dimension small = new Dimension(10, 0);
        playPauseButton = new JButton(mainFrame.getPlayMenu().getPlayAction());
        playPauseButton.setText(null);
        playPauseButton.setToolTipText("Play (Space)");

        JButton stopButton = new JButton(mainFrame.getPlayMenu().getStopAction());
        stopButton.setText("");
        stopButton.setToolTipText(NoteType.getCompoundName("Stop song", (KeyStroke) mainFrame.getPlayMenu().getStopAction().getValue(Action.ACCELERATOR_KEY)));

        JButton closeButton = new JButton(new CloseAction());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));

        for (Action a : actions) {
            southPanel.add(new JButton(a));
            KeyStroke[] ak = (KeyStroke[]) a.getValue(Constants.ACCELERATOR_KEYS);

            if (ak != null) {
                for (KeyStroke ks : ak) {
                    Object o = new Object();
                    musicSheetComponent.getInputMap(JComponent.WHEN_FOCUSED).put(ks, o);
                    musicSheetComponent.getActionMap().put(o, a);
                }
            }
        }

        southPanel.add(Box.createHorizontalGlue());

        if (sliderChangeListener != null) {
            tempoChangeSlider = PlaybackPanel.tempoChangeSliderFactory();
            tempoChangeSlider.addChangeListener(sliderChangeListener);
            tempoChangeSlider.setMaximumSize(new Dimension(50, 50));
            southPanel.add(tempoChangeSlider);
            southPanel.add(Box.createRigidArea(small));
        }

        Action as[] = { mainFrame.getPlayMenu().getPlayAction(), stopButton.getAction(), closeButton.getAction() };
        int i = 0;

        for (Action a : as) {
            KeyStroke ks = (KeyStroke) a.getValue(Action.ACCELERATOR_KEY);

            if (ks != null) {
                Object o = new Object();
                musicSheetComponent.getInputMap(JComponent.WHEN_FOCUSED).put(ks, o);
                musicSheetComponent.getActionMap().put(o, i != 0 ? a : new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        playPauseButton.doClick();
                    }
                });
            }

            i++;
        }

        southPanel.add(playPauseButton);
        southPanel.add(Box.createRigidArea(small));
        southPanel.add(stopButton);
        southPanel.add(Box.createRigidArea(small));
        southPanel.add(closeButton);
        southPanel.add(Box.createRigidArea(small));
        getContentPane().add(BorderLayout.SOUTH, southPanel);
    }

    public void setMusicSheet() {
        MusicSheet musicSheet = mainFrame.getMusicSheet();
        Component toolbar = getComponent(0);
        sheetSize = new Dimension(
                getWidth(),
                getHeight() - toolbar.getPreferredSize().height);

        zoom = Math.min(
                    ((double) sheetSize.width - (MARGIN * 2)) / musicSheet.getSheetWidth(),
                    ((double) sheetSize.height - MARGIN) / musicSheet.getSheetHeight());
        zoom = Math.min(zoom, MAX_ZOOM_RATIO);

        if (zoomedImage == null || zoomedImageWidth < sheetSize.width || zoomedImageHeight < sheetSize.height) {
            zoomedImageWidth = (int) (musicSheet.getSheetWidth() * zoom);
            zoomedImageHeight = (int) (musicSheet.getSheetHeight() * zoom);

            if (UIUtil.isRetina()) {
                zoomedImage = new JBHiDPIScaledImage(zoomedImageWidth, zoomedImageHeight, BufferedImage.TYPE_INT_RGB);
            }
            else {
                zoomedImage = new BufferedImage(zoomedImageWidth, zoomedImageHeight, BufferedImage.TYPE_INT_RGB);
            }
        }

        MyBorder border = new MyBorder(0, 0);
        musicSheetComponent.translate = new Point(0, 0);
        musicSheet.createMusicSheetImageForExport(zoomedImage, getBackground(), zoom, border);
        musicSheetComponent.setPreferredSize(new Dimension(sheetSize.width, sheetSize.height));
        repaint();
    }

    public void meta(MetaMessage meta) {
        // paint playing
        if (meta.getType() == 0) {
            byte[] data = meta.getData();
            musicSheetComponent.playingLine = data[0] << 8 | data[1];
            musicSheetComponent.playingNote = data[2] << 8 | data[3];
            musicSheetComponent.repaint();
        }
    }

    public void enableNotActionComponents(boolean enabled) {
        if (tempoChangeSlider != null) {
            tempoChangeSlider.setEnabled(enabled);
        }

        Action action = enabled ? mainFrame.getPlayMenu().getPlayAction() : mainFrame.getPlayMenu().getPauseAction();
        playPauseButton.setAction(action);
        playPauseButton.setText(null);
        playPauseButton.setToolTipText(action.getValue(Action.NAME) + " (Space)");
    }

    private class MusicSheetComponent extends JComponent implements FocusListener, Runnable {
        private Point translate;
        private int playingLine = -1, playingNote = -1;

        public MusicSheetComponent() {
            addFocusListener(this);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (zoomedImage != null) {
                int xTranslate = Math.max((sheetSize.width - zoomedImageWidth) / 2, 0);
                int yTranslate = Math.max((sheetSize.height - zoomedImageHeight) / 2, 0);
                g2.translate(xTranslate, yTranslate);
                UIUtil.drawImage(g2, zoomedImage, 0, 0, null);
            }

            // paint playing
            if (playingLine != -1 && playingNote != -1) {
                g2.translate(translate.x, translate.y);
                g2.scale(zoom, zoom);
                g2.translate(0, -mainFrame.getMusicSheet().getStartY());
                Line line = mainFrame.getMusicSheet().getComposition().getLine(playingLine);
                mainFrame.getMusicSheet().getBestDrawer().paintNote(g2, line.getNote(playingNote), playingLine,
                        line.getBeamings().findInterval(playingNote) != null, Color.magenta);
                playingLine = playingNote = -1;
            }
        }

        public void focusGained(FocusEvent e) {
        }

        public void focusLost(FocusEvent e) {
            new Thread(this).start();
        }

        public void run() {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                // pass
            }

            requestFocusInWindow();
        }
    }

    private class CloseAction extends AbstractAction {
        public CloseAction() {
            putValue(SMALL_ICON, new ImageIcon(MainFrame.getImage("windows_nofullscreen.png")));
            putValue(SHORT_DESCRIPTION, "Close full screen mode (Esc)");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        }

        public void actionPerformed(ActionEvent e) {
            if (MainFrame.sequencer != null) {
                MainFrame.sequencer.removeMetaEventListener(FullScreenSheet.this);
                MainFrame.sequencer.addMetaEventListener(mainFrame.getMusicSheet());
            }

            FullScreenSheet.this.setVisible(false);
            mainFrame.getPlayMenu().removePlaybackListener(FullScreenSheet.this);
        }
    }
}
