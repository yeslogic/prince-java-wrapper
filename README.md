# prince-java-wrapper

This library contains class files that provide a Java interface to [Prince][1].

## Examples

Using the default Prince interface:

```java
import com.princexml.wrapper.Prince;

public static void main(String[] args) {
    // Configure and build Prince:
    Prince p = Prince.builder("path/to/prince")
            .verbose(true)
            .licenseFile("path/to/license")
            .build();

    // Execute a job:
    boolean result = p.newJob()
            .javaScript(true)
            .script("path/to/script")
            .styleSheet("path/to/stylesheet")
            .convert("input", "output");
}
```

Using the Prince control interface:

```java
import com.princexml.wrapper.PrinceControl;

import java.io.FileOutputStream;

public static void main(String[] args) {
    // Configure and start the PrinceControl process:
    PrinceControl p = PrinceControl.builder("path/to/prince")
            .verbose(true)
            .licenseFile("path/to/license")
            .start();

    // Execute a job:
    try (FileOutputStream fos = new FileOutputStream("output.pdf")) {
        boolean result = p.newJob()
                .javaScript(true)
                .script("path/to/script")
                .styleSheet("path/to/stylesheet")
                .convert("input", fos);
    }

    // Stop the PrinceControl process:
    p.stop();
}
```

## Documentation

Documentation for the current version can be found [here][2]. To access the
documentation for a previous version, append the desired version number to the
URL, e.g.

> https://yeslogic.github.io/prince-java-wrapper/v1.0.0

## Download

This wrapper is hosted on [Maven Central][3].

[1]: https://www.princexml.com/
[2]: https://yeslogic.github.io/prince-java-wrapper/
[3]: https://search.maven.org/artifact/com.princexml/prince-java-wrapper
