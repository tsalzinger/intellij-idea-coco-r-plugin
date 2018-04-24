package at.scheinecker.intellij.coco;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static at.scheinecker.intellij.coco.psi.CocoTypes.*;

%%

%{
  private boolean hasDirectiveValue = false;

  public CocoLexer() {
    this((java.io.Reader)null);
  }
%}

%s DIRECTIVE

%public
%class CocoLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

hexValue = ([:digit:]|[A-F]|[a-f])
hexCharValue = \\u{hexValue}{4}
escapesequences = (\\\' | \\\" | \\\\ | \\0 | \\r | \\n | \\t | \\v | \\f | \\a | \\b | {hexCharValue})
EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+
STRING=\"([^\"\n\\]|{escapesequences})*?\"
CHAR='([^"'"]|{escapesequences}|" ")'
WHITE_SPACE=[ \t\n\x0B\f\r]+
LINE_COMMENT="//".*
BLOCK_COMMENT="/"\*([^\*]|\*+[^/\*])*\*+"/"
NUMBER=[:digit:]+
IDENT=[:letter:]([:letter:]|[:digit:])*
DIRECTIVE_NAME=\$[:letter:]+
ANY_CHAR=.

%%

<YYINITIAL> {
  {WHITE_SPACE}        { return com.intellij.psi.TokenType.WHITE_SPACE; }
  {DIRECTIVE_NAME}     { hasDirectiveValue = false; yybegin(DIRECTIVE); return DIRECTIVE_NAME; }

  "ANY"                { return KEYWORD_ANY; }
  "CASE"               { return KEYWORD_CASE; }
  "CHARACTERS"         { return KEYWORD_CHARACTERS; }
  "COMPILER"           { return KEYWORD_COMPILER; }
  "COMMENTS"           { return KEYWORD_COMMENTS; }
  "CONTEXT"            { return KEYWORD_CONTEXT; }
  "END"                { return KEYWORD_END; }
  "FROM"               { return KEYWORD_FROM; }
  "IF"                 { return KEYWORD_IF; }
  "IGNORE"             { return KEYWORD_IGNORE; }
  "IGNORECASE"         { return KEYWORD_IGNORECASE; }
  "import"             { return KEYWORD_IMPORT; }
  "NESTED"             { return KEYWORD_NESTED; }
  "out"                { return KEYWORD_OUT; }
  "PRAGMAS"            { return KEYWORD_PRAGMAS; }
  "PRODUCTIONS"        { return KEYWORD_PRODUCTIONS; }
  "SYNC"               { return KEYWORD_SYNC; }
  "TO"                 { return KEYWORD_TO; }
  "TOKENS"             { return KEYWORD_TOKENS; }
  "WEAK"               { return KEYWORD_WEAK; }
  "."                  { return TERMINATOR; }
  "+"                  { return PLUS; }
  "-"                  { return MINUS; }
  ".."                 { return RANGE; }
  "="                  { return ASSIGNMENT; }
  "{"                  { return CURL_OPEN; }
  "}"                  { return CURL_CLOSE; }
  "("                  { return PAR_OPEN; }
  ")"                  { return PAR_CLOSE; }
  "["                  { return BRACK_OPEN; }
  "]"                  { return BRACK_CLOSE; }
  "<."                 { return ATTRIBUTES_START; }
  ".>"                 { return ATTRIBUTES_END; }
  "<"                  { return SMALLER_THEN; }
  "(."                 { return SEM_ACTION_START; }
  ".)"                 { return SEM_ACTION_END; }
  "|"                  { return PIPE; }
  ">"                  { return GREATER_THEN; }

  {STRING}             { return STRING; }
  {CHAR}               { return CHAR; }
  {LINE_COMMENT}       { return LINE_COMMENT; }
  {BLOCK_COMMENT}      { return BLOCK_COMMENT; }
  {NUMBER}             { return NUMBER; }
  {IDENT}              { return IDENT; }
  {ANY_CHAR}           { return ANY_CHAR; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<DIRECTIVE> {
  "="                  { return ASSIGNMENT; }
  {WHITE_SPACE}        { yypushback(yylength()); yybegin(YYINITIAL); if (hasDirectiveValue) { return DIRECTIVE_VALUE; } }
  [^]                  { hasDirectiveValue = true; }
}
