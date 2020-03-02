# Spear

Spear is an android gradle plugin to facilitate writing Android instrumentation tests without the need to change your dagger setup. It does so by allowing mocking dagger modules.

In order to make this possible currently there is a limitation:
Your dagger modules need to be declared as kotlin objects.

**Spear** is comprised of three parts
1. Plugin, responsible for generating/rewriting byte code to facilitate replacing modules
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

The processor will generate an adapter

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

Which you can use in your tests to replace `MyModule` with a mocked instance:

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

You can use your mocking library of choice to mock the module (the example above I have used [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin))

For more details check out the [sample](sample/).
In order to run the sample first you need to publish all artifacts to maven local by issuing the following command
```
./gradlew publishToMavenLocal 
```
