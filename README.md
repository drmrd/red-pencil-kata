# Red Pencil Kata #

This repository contains my test-driven and Java-based solution to
Stefan Rock's
[Red Pencil Kata](https://stefanroock.wordpress.com/2011/03/04/red-pencil-code-kata/).

## Building and Testing from Source ##

I've included Gradle wrapper and build configuration files for easy
compilation and testing. Accordingly, even without Gradle installed,
you can easily build the project from source as follows:

  1. Clone this repository onto your machine
  2. From the project's root directory, run `./gradlew clean build`.
     This should setup the project for you, build class and jar files
     for the project, and compile and run its JUnit 4 tests.
  3. To rerun the tests, either run `./gradlew test` or `./gradlew
     clean test` to ensure that tests are rerun even if no changes
     have been made to the project.

     **Note:** On Windows, replace `./gradlew` with `gradlew.bat` in
     each of these commands.

## Possible Project Extensions ##

I am happy with how my solution turned out and enjoy that it solves
the problem while keeping things simple. It is also implemented in
such a way that it is readily extended/refactored to accommodate
different requests from the client/use cases. A couple natural
extensions/redesigns for more complicated requests from the client
(which could also be seen as extended programming exercises) are as
follows:

  * **Replace the `Promotion` class with a `Product` decorator.**
    According to the kata prompt, all products are supposed to be
    eligible for Red Pencil promotions. If this was not the case,
    especially if there were other optional features for products
    independent of promotion availability, this could be a good time
    to implement an abstract `ProductDecorator` class and replace the
    current `Promotion` class with a concrete
    `PromotionDecorator`. Implementing the other optional features as
    decorators would then simplify further development of the online
    marketplace and also encourage developers to abide by the Single
    Responsibility Principle.
  * **Refactor promotion rules using the Strategy pattern.** Given
    that the online marketplace's owner is concerned about sellers
    exploiting loopholes in promotion rules, the latter might
    continually need to be updated and subject to change as sellers
    come up with new and creative ways to cheat the system. They also
    could potentially need to vary based on type of product, demand,
    some metric of seller trustworthiness, product age, other products
    that are currently promoted, etc.

    If the promotion validation rules turn out to be this variable,
    encapsulating them into policies would dramatically simplify
    maintaining and extending the codebase.
