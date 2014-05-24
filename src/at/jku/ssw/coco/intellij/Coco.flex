package at.jku.ssw.coco.intellij;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import at.jku.ssw.coco.intellij.psi.CocoTypes;
import com.intellij.psi.TokenType;

%%

%class CocoLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
  return;
%eof}

CRLF= \n|\r|\r\n
WHITE_SPACE=[\ \t\f]
END_OF_LINE_COMMENT=("//")[^\r\n]*
SEPARATOR=[:=]

ident = [:jletter:] [:jletterdigit:]*
number = [:digit:] [:digit:]*
string = \"(.[^\"]*)\"
char = \'(.[^\'])\'

//ident ::= letter {letter | digit} // handled in lexer
//number ::= digit {digit} // handled in lexer
//string ::= '"' {anyButQuote} '"'
//char_ ::= "\'" anyButApostrophe "\'"


%state WAITING_VALUE

%%

<YYINITIAL> {ident}                                         { yybegin(YYINITIAL); return CocoTypes.IDENT; }
<YYINITIAL> {string}                                        { yybegin(YYINITIAL); return CocoTypes.STRING; }
<YYINITIAL> {char}                                          { yybegin(YYINITIAL); return CocoTypes.CHAR; }

.                                                           { return TokenType.BAD_CHARACTER; }