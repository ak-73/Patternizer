# Insert software patterns comfortably into Java source code
- an eclipse plug-in
- hooks into the context menu of your java file, right underneath "``Source``"
- Currently supported patterns: Singleton, Builder and Visitor


![Patternizer Singleton Demonstration](https://media.giphy.com/media/CLZK6kkOLRXbZaq6bI/giphy.gif)


State of the plugin: Functional but basic. 


# Installation Instructions
1. Clone the repo.
2. Make sure your Eclipse IDE has everything from the categories Eclipse Platform, 
Eclipse Platform SDK and Eclipse Plugin Development Tools installed (Help > Install New Software)
3. Go to Run > Run Configurations and run as "Eclipse Application". It should run right of the box 
without changing the default Run configuration.

# Usage
For usage, just go to the new "``Insert Pattern``" item in the context menu,
choose the desired pattern and configure the insertion. The exact nature of
the config dialog will depend on the pattern selected and the pattern 
implementations (variants) provided. For example, when selecting the Singleton
pattern you may choose between a simple but non-thread safe implementation or
an implementation based on the [initialization-on-demand holder] idiom of the 
Singleton pattern. 

# Notes

This plugin uses [org.reflections] for self-registering new pattern implementations. 

As a **Junior Developer**, it also features not only PDE/OSGi and JDT, SWT and JFace, JUnit 5 and SLF4J/Logback, but idioms such as fluent programming and lambda expressions, var and varargs, etc. here and there and reasonably clean, self-documenting and documented code. The source code itself is (ironically) light on design patterns, except for the occasional (abstract) factory; however, I intend to implement reconfiguration functionality, for example, to convert ``if (instanceof)`` code into a Visitor pattern, which is supposed to be applied to the Patternizer itself.

**Next up:** clean up, documentation and testing, then improvements of Visitor (ideally undoing _all_ changes in _all_ files with one hit of "Undo").

## License
The project is available under the **[EPL 2.0 License]**




[initialization-on-demand holder]: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
[org.reflections]: https://github.com/ronmamo/reflections
[EPL 2.0 License]: https://www.eclipse.org/legal/epl-2.0/