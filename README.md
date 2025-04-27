# Unification Algorithm in Java

This project implements the Martelli & Montanari unification algorithm for term algebra in Java. It provides a clean, immutable API for representing terms, substitutions, and an algorithm with step-by-step tracing.

## Features

- Term Representation: **Term** interface with concrete classes:
  - **Variable**
  - **Constant**
  - **Function**
- **Equation**: Encapsulates a pair of terms to unify.
- Immutable Substitution: **Substitution** class that maps variables to terms; supports composition and application.
- **Unifier**: Static Unifier.unify(...) method implementing the four rules:
  - *Delete*: Remove identical equations.
  - *Swap*: Swap sides if variable is on the right.
  - *Eliminate*: Bind variable to term with occurs-check.
  - *Decompose*: Split function equations into argument equations.
- **Trace Logging**: Each unification step logs:
  - Remaining equations
  - Current substitution
  - Applied rule and resulting binding
- JUnit 5 Tests: Comprehensive test suite covering constants, variables, functions, occurs-check, multi-equation unification, and trace validation.

## Getting Started
- Java 17 or higher
- Maven
- JUnit5 for tests

## Example
// TODO

## Project Structure
// TODO
