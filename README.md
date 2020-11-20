# Mockito for TestNG

Mockito utility classes for easy integration with TestNG

## Installation

### Gradle
```Gradle
dependencies {
  testCompile "org.mockito:mockito-testng:VERSION"
}
```

### Maven
```xml
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-testng</artifactId>
  <version>VERSION</version>
  <scope>test</scope>
</dependency>
```

For latest version, see the [release notes](/docs/release-notes.md).

## Usage

Simply add `@Listeners` annotation on your test class

```java
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Map;

@Listeners(MockitoTestNGListener.class)
public class MyTest {

    @Mock
    Map map;

    @InjectMocks
    SomeType someType;

    @Test
    void test() {
        // ...
    }
}
```

`MockitoTestNGListener` will do job for you and initialize all fields annotated with mockito annotations.

## Developing

- open in IDEA to develop or run ```./gradlew idea``` and then open in IDEA
- ```./gradlew build``` - tests code and assembles artifacts
- ```./gradlew publishToMavenLocal``` - publishes to local Maven repo, useful for local testing

## Releasing

Every merged pull request is published to JCenter and Maven Central.
Actually, any change on master that happily builds on Travis CI is published
unless the binaries are the same as the previous release.
The release automation uses Shipkit framework (http://shipkit.org).

## History

The original TestNGListener was a part of the core Mockito repository. However, the jar was never released. Core Mockito team does not work with TestNG so it was hard for us to maintain TestNG integration. In Summer 2018 we moved the TestNG integration to a separate repository under "Mockito" organization on GitHub.

## Help

We are looking for maintainers of TestNG integration! Let us know if you want to join. Mockito core team has limited capacity and focuses on the core library.
