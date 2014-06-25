\version "2.18.0"

\header {
    encodingsoftware = "Finale 2012 for Mac"
    encodingdate = "2014-03-10"
   arranger = \markup { 
       \override #'(baseline-skip . -1)
       \override #'(font-size . -2.5)
       \column { ${rightinfo} }  
   }
    title =  \markup {\override #'(font-size . 3) \line { ${title} }  }    
    tagline = \markup { \override #'(font-size . -2.5) \line { "Copyright © 2014 Sri Chinmoy              All rights reserved under Creative Commons license 3.0"}  }    
}

#(set-global-staff-size 24.0900948425)
\paper {
    paper-width = 21.59\cm
    paper-height = 27.94\cm
    top-margin = 1.27\cm
    bottom-margin = 1.27\cm
    left-margin = 1.27\cm
    right-margin = 1.27\cm
    between-system-space = 1.61\cm
    page-top-space = 0.7\cm

  system-system-spacing = 
    #'((basic-distance . 11.2) 
       (minimum-distance . 10) 
       (padding . 1) 
       (stretchability . 60)) 


     myStaffSize = #24.09
     indent=0\cm
  #(define fonts
    (make-pango-font-tree "Georgia"  "Arial"  "Arial"  (/ myStaffSize 20  )))
}

    
PartPOneVoiceOne =  \absolute {
\autoBeamOff            
\override Staff.BarLine #'break-visibility = #'#(#t #f #t)
  \omit Staff.TimeSignature
  \omit Score.BarNumber
    \set Lyrics.includeGraceNotes = ##t
 % \set Staff.autoBeaming = ##f
 % \override Voice.MetronomeMark.font-size = #20 
   \override Score.MetronomeMark #'font-size = -2.5
    \override Stem.neutral-direction = #up 
    \override Lyrics.LyricHyphen #'dash-period = #2.2
    \override Lyrics.LyricText #'font-size = #-1
    \override Glissando #'style = #'trill
      \override Score.Glissando #'(bound-details left padding) = #0.5
      	\override Staff.Slur.stencil = ##f
    

        \tempo ${tempo}
      
        \clef "treble" ${key} 
        ${score} 
    }

PartPOneVoiceOneLyricsOne =  \lyricmode {
 %   \set includeGraceNotes = ##t
  ${lyrics} }

% The score definition
\score {
    <<
        \new Staff <<
            \context Staff << 
                \context Voice = "PartPOneVoiceOne" { \PartPOneVoiceOne }
                \new Lyrics \lyricsto "PartPOneVoiceOne" \PartPOneVoiceOneLyricsOne
                >>
            >>
        
        >>
    \layout {}
    % To create MIDI output, uncomment the following line:
      \midi {}
    }
