# Spear

Spear is an android gradle plugin to facilitate writing Android instrumentation tests without the need to change your dagger setup. It does so by allowing
1. Mocking dagger modules declared as kotlin objects
2. Changing the instance returned by generated dagger `Factory` implementations

**Spear** is comprised of three parts
1. Plugin, responsible for generating/rewriting byte code to facilitate replacing modules and instances returned by generated dagger `Factory` implementations
2. Annotation processor, that generates adapter classes to replace and reset dagger modules
3. Adapter library, containing the module adapter interface

## Usage

In your project add [JirPack](https://jitpack.io/) to buildscript and project Dependencies
```
buildscript {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}

allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

In the android modules where you wish to mock out module dependencies apply the Plugin
```
apply plugin: 'hu.nemi.spear'
```

Add processor and adapter as debug Dependencies
```
dependencies {
 kaptDebug "com.github.mos8502.spear:processor:<Tag>"
 debugImplementation "com.github.mos8502.spear:adapter:<Tag>"
}
```

For any module
```
package my.package

@Module
object MyModule {
  ...

  @Provides
  fun provideFoo(foo: FooImpl): Foo = foo
}
```

The processor will generate an adapter for the module

```
package my.package

object MyModuleAdapter {
  MyModule.replaceWith(module: MyModule) {
    ...
  }

  MyModule.reset() {
    ...
  }

}
```

And an adapter class for the type returned by `MyModule.provideFoo`

```
package my.package

object FooAdapter {
  var shadow: Foo?
}
```
In your test you can use the generated module adapter to replace the whole module with a mockable implementation

```
import my.package.MyModule
import my.package.MyModuleAdapter.replaceWith
import my.package.MyModuleAdapter.reset

import com.nhaarman.mockitokotlin2.mock

class MyTest {

  @Mock lateinit var foo: Foo

  @Before
  fun setup() {
    // initialize mocks
    MockitoAnnotations.initMocks(this)

    // replace module with mock module calling through to real methods
    MyModule replaceWith mock(defaultAnswer = CallsRealMethods())

    // return mocked foo from module
    doReturn(foo).whenever(MyModule).provideFoo(any())
  }

  @After
  fun teardown() {
    // reset module by replacing instance with an un mocked version
    MyModule.reset()
  }
}
```

Alternatively you can use the generated dependency adapters to mock you Dependencies
```
import my.package.FooAdapter

import com.nhaarman.mockitokotlin2.mock

class MyTest {

  @Mock lateinit var foo: Foo

  @Before
  fun setup() {
    // initialize mocks
    MockitoAnnotations.initMocks(this)

    FooAdapter.shadow = foo

    // return mocked foo from module
    doReturn(foo).whenever(MyModule).provideFoo(any())
  }

  @After
  fun teardown() {
    FooAdapter.shadow = null
  }
}
```

You can use your mocking library of choice to mock the modules or individual dependencies (the example above I have used [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin))

For more details check out the [sample](sample/).
In order to run the sample first you need to publish all artifacts to maven local by issuing the following command
```
./gradlew publishToMavenLocal
```
