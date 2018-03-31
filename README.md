# Coco/R Plugin for IntelliJ IDEA

This plugin adds support for [Coco/R](http://ssw.jku.at/coco) to the IntelliJ IDEA Platform.
The initial version was developed as part of a bachelor theses and the first public release was on 2018-01-25.
Maintenance will be performed on a best effort basis.

## Plugin Installation

The plugin can be installed directly from the official Jetbrains Plugin Repository or via the zip file provided on the [Releases](https://github.com/tscheinecker/intellij-idea-coco-r-plugin/releases) page.

## Plugin features 

* Syntax Highlighting - Customizable via settings page
* Commenter (Block and line comments)
* Syntax Error highlighting (unexpected tokens)
* Reference check (report unresolvable references to characters, tokens and productions)
* Declaration order check (report references to characters and tokens declared after usage)
* Attributes check (productions have to be used with or without attributes according to declaration)
* Highlight unused characters, tokens, pragmas and productions
* Code Folding
* Structure View
* Find usages
* Inplace rename refactorings (Compiler, Characters, Tokens, Pragmas and Productions)
* Brace matching for (), [], {}, <>, <..> and (..)
* Code completion (Compiler, Character, Token, Pragma and Production names)
* Go to declaration
* Go to symbol / Symbol search
* Go to generated code (for productions)
* Configurable Java language injection
* Generate Scanner and Parser via menu action
* Automatic detection of appropriate output folder for generated Java classes
* Automatic detection of package in case no $package directive is pre
* Automatic detection of appropriate output root for generated Java classes
* Warnings and Errors during Coco/R execution are shown within the Problems Tool Window
* Errors from generated Parser are mapped back to the grammar file

## Documentation

Please refer to the attached PDF on the [Releases](https://github.com/tscheinecker/intellij-idea-coco-r-plugin/releases) page.
Additional documentation (as part of the README or via Wiki) will be added in the future.  

## IDE support limitations

In it's current state the plugin is designed specifically for usage with the Java version of Coco/R.
The language for global fields and methods, as well as semantic actions is always assumed to be Java, which will also be injected.
Due to this fact the plugin currently cannot be used with any IDEs not including Java support (e.g. Rider, WebStorm, RubyMine, ...). 
If you still want to use Coco/R in any of these IDEs or a non Java version of Coco/R you have some options:
* **Send me a Pull request**
* Be patient until I get around to decouple the plugins dependency from Java support
* Or use https://github.com/azrdev/coco-idea-plugin/ as an alternative  

## License

The plugin is licensed under the [GNU General Public License v3.0](./LICENSE)