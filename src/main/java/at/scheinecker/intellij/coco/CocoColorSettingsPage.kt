package at.scheinecker.intellij.coco

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

/**
 * @author Thomas Scheinecker [tscheinecker@gmail.com](mailto:tscheinecker@gmail.com)
 */
class CocoColorSettingsPage : ColorSettingsPage {

    override fun getIcon(): Icon? {
        return CocoIcons.FILE
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return CocoSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return COLOR_SETTINGS_ATG_CONTENT
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? {
        return null
    }

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return DESCRIPTORS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return emptyArray()
    }

    override fun getDisplayName(): String {
        return "Cocol/R"
    }

    companion object {
        private val DESCRIPTORS = arrayOf(AttributesDescriptor("Block Comment", CocoSyntaxHighlighter.BLOCK_COMMENT), AttributesDescriptor("Line Comment", CocoSyntaxHighlighter.LINE_COMMENT), AttributesDescriptor("Keyword", CocoSyntaxHighlighter.KEYWORD), AttributesDescriptor("Identifier", CocoSyntaxHighlighter.IDENTIFIER), AttributesDescriptor("Markup Tags", CocoSyntaxHighlighter.MARKUP_TAG), AttributesDescriptor("Char", CocoSyntaxHighlighter.CHAR), AttributesDescriptor("String", CocoSyntaxHighlighter.STRING), AttributesDescriptor("Braces", CocoSyntaxHighlighter.BRACES), AttributesDescriptor("Brackets", CocoSyntaxHighlighter.BRACKETS), AttributesDescriptor("Parentheses", CocoSyntaxHighlighter.PARENTHESES), AttributesDescriptor("Operations ( + | - | .. | = )", CocoSyntaxHighlighter.OPERATION_SIGN), AttributesDescriptor("Terminal Symbol", CocoSyntaxHighlighter.TERMINATOR))
        private val COLOR_SETTINGS_ATG_CONTENT = """COMPILER Taste

Proc curProc;  // current program unit (procedure or main program)

CHARACTERS
  letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".
  digit = "0123456789".
  cr  = '\r'.
  lf  = '\n'.
  tab = '\t'.

TOKENS
  ident  = letter {letter | digit}.
  number = digit {digit}.

COMMENTS FROM "/*" TO "*/" NESTED
COMMENTS FROM "//" TO lf

IGNORE cr + lf + tab

PRODUCTIONS

/*=================== Program and declarations ============================*/

Taste                    (. String name; .)
= "program"
  Ident<out name>        (. curProc = new Proc(name, null, this); .)
  "{"
  { VarDecl | ProcDecl }
  "}"                    (. for (Obj obj: curProc.locals) {
                            	if (obj instanceof Proc) ((Proc)obj).dump();
                            } .).
/*------------------------------------------------------------------------*/
VarDecl                  (. String name; Type type; .)
= Typ<out type>
  Ident<out name>        (. curProc.add(new Var(name, type)); .)
  { ',' Ident<out name>  (. curProc.add(new Var(name, type)); .)
  } ';'.
/*------------------------------------------------------------------------*/
Typ<out Type type>
=                        (. type = Type.INT; .)
 ( "int"
 | "bool"                (. type = Type.BOOL; .)
 ).
/*------------------------------------------------------------------------*/
ProcDecl                 (. String name; .)
= "void"
  Ident<out name>        (. Proc oldProc = curProc;
                            curProc = new Proc(name, oldProc, this);
                            oldProc.add(curProc); .)
  '(' ')'
  Block<out curProc.block> (. curProc = oldProc; .).


/*============================= Statements ===============================*/

Block<out Block b>       (. Stat s; .)
= '{'                    (. b = new Block(); .)
  { Stat<out s>          (. b.add(s); .)
  | VarDecl
  }
  '}'
  .
/*------------------------------------------------------------------------*/
Stat<out Stat s>         (. String name; Expr e; Stat s2; Block b; .)
=                        (. s = null; .)
( Ident<out name>        (. Obj obj = curProc.find(name); .)
  ( '='
		Expr<out e> ';'      (. s = new Assignment(obj, e); .)
	| '(' ')' ';'          (. s = new Call(obj); .)
	)

| "if"
	'(' Expr<out e> ')'
	Stat<out s>            (. s = new If(e, s); .)
	[ "else" Stat<out s2>  (. s = new IfElse(s, s2); .)
	]

| "while"
	'(' Expr<out e> ')'
	Stat<out s>            (. s = new While(e, s); .)

| "read"
	Ident<out name> ';'    (. s = new Read(curProc.find(name)); .)

| "write"
	Expr<out e> ';'        (. s = new Write(e); .)

| Block<out b>           (. s = b; .)
).



/*============================ Expressions ===============================*/

Expr<out Expr e>         (. Operator op; Expr e2; .)
= SimExpr<out e>
  [ RelOp<out op>
    SimExpr<out e2>      (. e = new BinExpr(e, op, e2); .)
  ].
/*------------------------------------------------------------------------*/
SimExpr<out Expr e>      (. Operator op; Expr e2; .)
= Term<out e>
  { AddOp<out op>
    Term<out e2>         (. e = new BinExpr(e, op, e2); .)
	}.
/*------------------------------------------------------------------------*/
Term<out Expr e>         (. Operator op; Expr e2; .)
= Factor<out e>
  { MulOp<out op>
    Factor<out e2>       (. e = new BinExpr(e, op, e2); .)
	}.
/*------------------------------------------------------------------------*/
Factor<out Expr e>       (. String name; .)
=                        (. e = null; .)
  ( Ident<out name>      (. e = new Ident(curProc.find(name)); .)
  | number               (. e = new IntCon(Integer.parseInt(t.val)); .)
  | '-' Factor<out e>    (. e = new UnaryExpr(Operator.SUB, e); .)
  | "true"               (. e = new BoolCon(true); .)
  | "false"              (. e = new BoolCon(false); .)
  ).
/*------------------------------------------------------------------------*/
Ident<out String name>
= ident                  (. name = t.val; .).
/*------------------------------------------------------------------------*/
AddOp<out Operator op>
=                        (. op = Operator.ADD; .)
  ( '+'
  | '-'                  (. op = Operator.SUB; .)
  ).
/*------------------------------------------------------------------------*/
MulOp<out Operator op>
=                        (. op = Operator.MUL; .)
  ( '*'
  | '/'                  (. op = Operator.DIV; .)
  ).
/*------------------------------------------------------------------------*/
RelOp<out Operator op>
=                        (. op = Operator.EQU; .)
  ( "=="
  | '<'                  (. op = Operator.LSS; .)
  | '>'                  (. op = Operator.GTR; .)
  ).

END Taste.
"""
    }
}
