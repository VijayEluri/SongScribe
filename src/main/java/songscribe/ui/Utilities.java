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

Created on Aug 6, 2006
*/
package songscribe.ui;

import org.apache.log4j.Logger;
import songscribe.Version;
import songscribe.data.GifEncoder;
import songscribe.data.MyDesktop;
import sun.font.Font2D;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Csaba Kávai
 */
public class Utilities {
    private static Logger logger = Logger.getLogger(Utilities.class);

    public static String getSongTitleFileNameForFileChooser(MusicSheet musicSheet){
        StringBuilder sb = new StringBuilder(musicSheet.getComposition().getSongTitle().length()+10);
        try{
            int number = Integer.parseInt(musicSheet.getComposition().getNumber());
            sb.append(String.format("%03d", number));
        }catch(NumberFormatException nfe){
            sb.append(musicSheet.getComposition().getNumber());
        }
        if(musicSheet.getComposition().getNumber().length()>0)sb.append(' ');
        for(char c : musicSheet.getComposition().getSongTitle().toCharArray()){
            Character specialCharMapped = mapSpecialChar(c);
            if (specialCharMapped != null) {
                sb.append(specialCharMapped);
            } else if (Character.isLetterOrDigit(c) || c == ' ') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static Character mapSpecialChar(char c) {
        for(int i=0;i<LyricsDialog.specChars.length;i++){
            for(int j=0;j<LyricsDialog.specChars[i].length;j++){
                if(c==LyricsDialog.specChars[i][j]){
                    return LyricsDialog.specCharsMap[i][j];
                }
            }
        }
        return null;
    }

    public static int arrayIndexOf(Object[] array, Object element){
        for(int i=0;i<array.length;i++){
            if(element.equals(array[i])){
                return i;
            }
        }
        return -1;
    }

    public static int lineCount(String str){
        if(str.length()==0)return 0;
        int found = 0;
        for(char ch:str.toCharArray()){
            if(ch=='\n')found++;
        }
        if(str.charAt(str.length()-1)!='\n')found++;
        return found;
    }

    public static void readComboValuesFromFile(JComboBox combo, File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line=br.readLine())!=null){
                combo.addItem(line);
            }
            br.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Could not open a necessary file. Please reinstall the software.", MainFrame.PACKAGENAME, JOptionPane.ERROR_MESSAGE);
            logger.error("readComboValuesFromFile open", e);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not open a necessary file. Please reinstall the software.", MainFrame.PACKAGENAME, JOptionPane.ERROR_MESSAGE);
            logger.error("readComboValuesFromFile open", e);
        }
    }

    private static boolean isMac;
    private static boolean isWindows;
    private static boolean isLinux;

    // An array of every actual font in the system, including all stylistic variations.
    private static Font[] systemFonts;

    // An array of base font names for each font in systemFonts.
    // For example, if the systemFont name is "MyriadPro-It", the base name is "MyriadPro".
    // This saves a lot of time when trying to find font names.
    private static String[] systemFontBaseNames;

    // For each font style, there are several possible font name suffix components
    // that might appear in the full font name.
    private static String[] plainFontSuffixNames = {"", "Regular", "Medium"};
    private static String[] italicFontSuffixNames = {"It", "Italic", "Oblique", "ItalicMT"};
    private static String[] boldFontSuffixNames = {"Bold", "Semibold", "Demibold", "BoldMT"};

    static{
        isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
        isLinux = System.getProperty("os.name").toLowerCase().contains("linux");

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        systemFonts = ge.getAllFonts();
        systemFontBaseNames = new String[systemFonts.length];

        for (int i = 0; i < systemFonts.length; ++i)
        {
            Font font = systemFonts[i];
            String name = font.getFontName();
            String[] parts = name.split("-");
            systemFontBaseNames[i] = parts[0];
        }
    }

    public static boolean isMac() {
        return isMac;
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isLinux() {
        return isLinux;
    }

    /*
        Given a font, base font name, and set of suffixes to add to the base name,
        return the matching base+suffix. If exactMatch is false, it's a match if
        the font name starts with base+suffix. If there is no match, return null.
    */
    private static String matchFontName(Font font, String baseName, String[] suffixes, boolean exactMatch) {
        String fontName = font.getFontName();
        boolean hasSuffix = baseName.contains("-");

        for (String suffix : suffixes) {
            if (!hasSuffix && suffix.length() > 0)
                suffix = "-" + suffix;

            String name = baseName + suffix;

            if (exactMatch) {
                if (fontName.equals(name))
                    return name;
            }
            else if (fontName.startsWith(name))
                return name;
        }

        return null;
    }

    /*
        Major hack alert!

        There is a bug in Oracle Java's Font2D.setStyle method
        that does not set the style of the Font2D correctly for
        some fonts, because the style matching algorithm is broken.
        This method forces the Font2D style to be the requested style.
    */
    public static void fixFont2DStyle(Font font, int style) {
        try {
            Class<?>[] params = new Class[0];
            Method method = font.getClass().getDeclaredMethod("getFont2D", params);
            method.setAccessible(true);
            Font2D font2d = (Font2D) method.invoke(font);

            if (font2d != null) {
                // The style field we want to set is in the Font2D class, get that class
                Field styleField = Font2D.class.getDeclaredField("style");
                styleField.setAccessible(true);
                styleField.setInt(font2d, style);
            }
        }
        catch (Exception ex) {
            // Oh well, we tried
        }
    }

    /*
        The built in Java font matching algorithms are pretty useless.
        This method will return the closest matching font for a given style.
        If bold+italic is requested, it will fall back to bold if available.
        Any style other than plain if not available will fall back to plain.
        If plain is not available, null is returned.

        The fixFont2D hack is applied if a matching font is found.
    */
    public static Font createFont(String familyName, int style, int size) {
        Font foundFont = null;

        for (int i = 0; i < systemFonts.length; ++i) {
            Font font = systemFonts[i];

            if (font.getFamily().equals(familyName)) {
                String baseName = systemFontBaseNames[i];
                String fontName = font.getFontName();

                if (style == Font.PLAIN) {
                    String name = matchFontName(font, baseName, plainFontSuffixNames, true);

                    // If we can't find the plain font, fail
                    if (name != null) {
                        foundFont = font;
                        break;
                    }
                }
                else if ((style & Font.BOLD) != 0) {
                    boolean wantItalic = (style & Font.ITALIC) != 0;
                    String name = matchFontName(font, baseName, boldFontSuffixNames, !wantItalic);

                    // If italic is also desired, try adding italic suffixes
                    if (name != null && wantItalic) {
                        if (wantItalic)
                            name = matchFontName(font, name, italicFontSuffixNames, true);
                    }

                    if (name != null) {
                        foundFont = font;
                        break;
                    }
                }
                else if (style == Font.ITALIC) {
                    String name = matchFontName(font, baseName, italicFontSuffixNames, true);

                    if (name != null) {
                        foundFont = font;
                        break;
                    }
                }
            }
        }

        if (foundFont == null) {
            if (style == (Font.BOLD | Font.ITALIC)) {
                // If we can't find bold+italic, fall back to bold
                foundFont = createFont(familyName, Font.BOLD, size);
            }
            else if (style != Font.PLAIN) {
                // If we get here and no styled font is found, fall back to plain.
                foundFont = createFont(familyName, Font.PLAIN, size);
            }
        }

        // If all attempts fail, use Java's built in search
        if (foundFont == null) {
            foundFont = new Font(familyName, style, size);
        }
        else {
            // Always use plain for the style, if we get here we have
            // a styled font variant already.
            foundFont = foundFont.deriveFont(Font.PLAIN, (float)size);
            fixFont2DStyle(foundFont, style);
        }

        return foundFont;
    }

    public static Font deriveFont(Font font, int style, int size) {
        return createFont(font.getFamily(), style, size);
    }

    public static String getPublicVersion() {
        return Version.PUBLIC_VERSION;
    }

    public static String getFullVersion() {
        return Version.PUBLIC_VERSION + "." + Version.BUILD_VERSION;
    }

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static void copyFile(File in, File out) throws IOException {
        FileInputStream fis  = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int i;
        while((i=fis.read(buf))!=-1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }

    public static void openExportFile(MainFrame mainFrame, File file){
        if(MyDesktop.isDesktopSupported()){
            MyDesktop desktop = MyDesktop.getDesktop();
            if(JOptionPane.showConfirmDialog(mainFrame, "Do you want to open the file?", mainFrame.PROGNAME, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION){
                try {
                    desktop.open(file);
                } catch (Exception e) {
                    mainFrame.showErrorMessage("Could not open the file.");
                }
            }
        }
    }

    public static void openWebPage(MainFrame mainFrame, String webPage){
        if(MyDesktop.isDesktopSupported()){
            MyDesktop desktop = MyDesktop.getDesktop();
            try {
                desktop.browse(new URI(webPage));
            } catch (Exception e) {
                mainFrame.showErrorMessage("Could not open the webpage.");
            }
        }
    }

    public static void openEmail(MainFrame mainFrame, String email){
        if(MyDesktop.isDesktopSupported()){
            MyDesktop desktop = MyDesktop.getDesktop();
            try {
                desktop.mail(new URI("mailto", email, null));
            } catch (Exception e) {
                mainFrame.showErrorMessage("Could not open the webpage.");
            }
        }
    }

    public static boolean writeImage(BufferedImage image, String extension, File file) throws IOException, AWTException {
        boolean successful = ImageIO.write(image, extension, file);
        if (!successful && extension.equalsIgnoreCase("gif")) {
            GifEncoder.writeFile(image, file);
            successful = true;
        }
        return successful;
    }

    public static String zipFile(ZipOutputStream zos, File file, String requestName, byte[] buf) throws IOException {
        String fileName = requestName == null ? file.getName() : requestName;
        zos.putNextEntry(new ZipEntry(fileName));
        FileInputStream fis = new FileInputStream(file);
        int read;
        while((read=fis.read(buf))>0){
            zos.write(buf, 0, read);
        }
        fis.close();
        return fileName;
    }


    public static String removeSyllablifyMarkings(String lyrics) {
        char[] lyricsChars = lyrics.toCharArray();
        boolean inParanthesis = false;
        StringBuilder sb = new StringBuilder(lyrics.length());
        for(int i=0;i<lyricsChars.length;i++){
            char c = lyricsChars[i];
            if(c=='(')inParanthesis=true;
            if(!inParanthesis){
                if(c!='-' && c!='_'){
                    sb.append(c);
                }else if(c=='-' && i<lyricsChars.length-1 && lyricsChars[i+1]=='-'){
                    sb.append('-');
                }
            }
            if(c==')')inParanthesis=false;
        }
        return sb.toString();
    }
}
