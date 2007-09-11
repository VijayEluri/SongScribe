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

Created on Feb 18, 2006
*/
package songscribe.IO;

import java.io.PrintWriter;
import java.io.IOException;

/**
 * @author Csaba Kávai
 */
public class XML {
    public static int indent;

    public static void writeEmptyTag(PrintWriter pw, String tag) throws IOException{
        for(int i=0;i<indent;i++)pw.print(" ");
        pw.print("<");pw.print(tag);pw.println(" />");
    }

    public static void writeBeginTag(PrintWriter pw, String tag) throws IOException{
        for(int i=0;i<indent;i++)pw.print(" ");
        pw.print("<");pw.print(tag);pw.println(">");
    }

    public static void writeEndTag(PrintWriter pw, String tag) throws IOException{
        for(int i=0;i<indent;i++)pw.print(" ");
        pw.print("</");pw.print(tag);pw.println(">");
    }

    public static void writeValue(PrintWriter pw, String tag, String value){
        for(int i=0;i<indent;i++)pw.print(" ");
        pw.print("<");pw.print(tag);pw.print(">");
        pw.print(value);
        pw.print("</");pw.print(tag);pw.println(">");
    }
}
