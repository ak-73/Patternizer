# Insert software design patterns comfortably into Java source code
- an eclipse plug-in
- hooks into the context menu of your java file, right underneath ```Source```
- Currently supported patterns: Singleton and Builder 

# Usage
For usage, just go to the new ```Insert Pattern``` item in the context menu,
choose the desired pattern and configure the insertion. The exact nature of
the config dialog will depend on the pattern selected and the pattern 
implementations (variants) provided. For example, when selecting the Singleton
pattern you may choose between a simple but non-thread safe implementation or
an implementation based on the [initialization-on-demand holder] idiom of the 
Singleton pattern. 

## License
The project is available under the **[EPL 2.0 License]**




[initialization-on-demand holder]: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
[EPL 2.0 License]: https://www.eclipse.org/legal/epl-2.0/