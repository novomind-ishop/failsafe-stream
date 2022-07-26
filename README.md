# Failsafe Stream API

This API helps to wrap functions in stream contexts,
that tend to throw any sort of exception.

Since the exception-support of the Java-Stream-API is
lacking some convenience regarding exception handling, 
this API copes with the thrown exception and enables ongoing
evaluation to continue despite any error at evaluating one
element at a step.

In general this API is feasible for features that use
lambdas as parameters.

# The Basis
Using Java-Libraries and built-in APIs is useful, speeds up the development process,
helps to increase the maintainability and understandability of the code.
Concepts like fluent method calls are a key feature enabling or optimizing functional programming.

## Stream-API
The Stream-API is based on the functional programming concept, designed to break down a
conversion or evaluation process using lambda-features and method references.

Each step can be one of the following types:
* evaluation / filtering (e.g. <code>filter(p:Predicate<>)</code>)
* conversion / mapping (e.g. <code>map(f:Function<A,B>)</code>)
* creation / reducing (e.g. <code>map(o:BinaryOperator<,>)</code>)
* ordering / comparing (e.g. <code>sorted(c:Comparator<>)</code>)
* collecting (e.g. <code>collect(c:Collector<>)</code>)
* handling (e.g. <code>forEach(h:Consumer<>)</code>)

_These steps are also present in the Optional-API._

Lambdas and method-references still miss a convenient way to 
switch between checked exceptions and unchecked exceptions in the method signature.

## Exceptions
Error-handling in Java involves the "throws" declaration in the method signature.
This leads to clashes in the expected and actual lambda signature.
Stream-API-Lambdas are expected not throw any kind of checked exception.

# The Problems:
The Stream-API is a design pattern that lets data-streams be handled fluently,
breaking down each conversion step by a single lambda or method reference.
Each element is handed over to the given lambda call and is then converted, evaluated, collected or just handled.
Each of these steps can - accidentally or by design - involve exceptions to be thrown.

## Problem 1
_No tolerance to thrown exceptions in the method signature._
<br/>
<code>FunctionalInterface</code>s of the Stream-API realm include no "throws" part.
That means exceptions cannot be thrown by any of the lambdas.

## Problem 2
_Any exception at one of the elements will terminate the whole stream._
<br/>
The Stream-API will terminate evaluation of all further elements, once one of the precessing steps throws a <code>RuntimeException</code>.
In order to perform a "best-afford-approach" for the evaluation and being tolerant to any errors it is necessary to catch all upcoming exceptions inside of the lambda.

# The Solution
The failsafe-stream API integrates into the mentioned mechanism and copes with the given back draws of the Stream-API.

## Example : Building a failsafe stream
Given is a List of Strings that are rudely filtered by a dangerous <code>Predicate</code>, mapped by a exception prone <code>Function</code>
before being collected into a List via best-afford concept.

<code>
...<br/>
&emsp;&emsp;elements.stream()
</code>

_// likely to fail via IndexOutOfBoundsException:_<br/>
<code>
&emsp;&emsp;&emsp;&emsp;.filter(s -> s.substring(4, 5).equals("x"))
</code>

_// likely to fail due to method being called_<br/>
<code>
&emsp;&emsp;&emsp;&emsp;.map(s -> this.mapStringToObjectOrThrowException(s))
&emsp;&emsp;.collect(Collectors.toList()); <br/>
}
<br/>
<br/>
private Object mapStringToObjectOrThrowException(String s) throws ArbitraryCheckedException {
<br/>
...
<br/>
}
</code>

These lines show several problems.
1. the filter-function will fail for all Strings shorter than 9
2. the map-function will fail if the called method fails
3. the map-function does not compile due to the unhandled _ArbitraryCheckedException_

In order to address these Problems, the following code will wrap the Exception-throwing
method call and just log every error while processing.

...<br/>
elements.stream()
<br/>
.filter(<br/>
&emsp;&emsp;FailsafeStream.<String, Exception> failsafeForTest(LOGGER)<br/>
&emsp;&emsp;&emsp;&emsp;.filter(s -> s.substring(4, 5).equals("x"))<br/>&emsp;&emsp;.perform()
<br/>)
.map(<br/>
&emsp;&emsp;FailsafeStream.<String, Object, Exception> failsafeForMap(LOGGER)<br/>
&emsp;&emsp;&emsp;&emsp;.map(this::mapStringToObjectOrThrowException)
<br/>&emsp;&emsp;.perform()
<br/>)
.collect(Collectors.toList()); <br/>

# Example : evaluation via filter-method
The <code>FunctionalInterface</code> of the <code>filter</code>-method-parameter of the
Stream-API is the <code>Predicate</code>.
Any Predicate-like method throwing exceptions is of the general interface
<br/>
<code><T, E extends Exception> boolean testMethod(T element) throws E;</code>
<br/>
whereas the <code>Predicate</code> function of the Stream-API is defined as
<br/>
<code>&lt;T&gt; boolean testMethod(T element);</code>

This clashes with any code evaluation regarding consistency of the "throws" definition.
In order to align the exception throwing methods signature with the original Predicate,
it's necessary to handle the exception expected to be thrown while filtering one element
and convert this function to the <code>Predicate</code> signature.


