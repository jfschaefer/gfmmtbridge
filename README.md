# gfmmtbridge

If you want to learn how to use the gfmmtbridge, you should take a look at the section *Example1*,
which serves as a tutorial.


## Setup

### Prerequisites

* A recent version of the grammatical framework (`gf`).
* MMT (see [https://uniformal.github.io/doc/setup/](https://uniformal.github.io/doc/setup/)). I used the 13th release. Other versions should work as well, just make sure that `scalaVersion` is set to the correct value in the `build.sbt` file during the setup - otherwise you might get very misleading error messages.
* Intellij (you can get it for free with your @fau.de email address) and the *Scala* plugin.


### Setup gfmmtbridge

* Clone the `gfmmtbridge` repository.
* Set the correct location of `mmt.jar` in the `build.sbt` file and change `scalaVersion` if necessary.
* Open Intellij. In case you decided to use another IDE, the rest of the setup won't apply to you.
* Go to *File* -> *New* -> *Project from Existing Sources...*. Select the `build.sbt` file. Click through the setup wizard - everything should be configured correctly. It takes a moment until everything is set up.
* On the bottom of the window, click on *sbt shell* and enter the command `compile`. This will probably take a while (for me 20s).
* Next, we should try to run the *Example1*.
For this need to create a new run configuration.
Click on the *Add Configuration* button (for me it's on the top right).
Click on the *+* (top left).
Go to *Scala Script*.
Name it *Example1*.
Select `examples/Example1.
cala` for the script file.
The other settings should be okay.
Click *Ok*.
After changing some paths in the script (see section *Example1*), you should be able to run the script (green triangle).


## Components of the `gfmmtbridge`

### `GfParser`

The `GfParser` is an interface (trait) for parsing sentences with a GF grammar.
The result is a list of possible parse trees.
Previously, this was done using GF's Java bindings, which were difficult
to install.
Instead, a `ServerGfParser` is provided. GF comes with a server and it's API supports parsing strings.
Therefore, a server process is started locally and every sentence is sent to this server for parsing.

### `GfMmtBridge`

The `GfMmtBridge` is an mmt-`Extension`.
It uses a `GfParser` to parse sentences, transform the parse trees to OMDoc based on an MMT theory, and expand the definitions of the resulting terms.
As one sentence can have multiple parse trees, you can get several terms for one sentence.



## Example1

`examples/Example1.scala` contains a simple example application that uses the `gfmmtbridge`.
It is intended to serve as a tutorial.
The MMT and GF files can be found in [gl.mathhub.info/Teaching/LBS](gl.mathhub.info/Teaching/LBS)

The script takes a sequence of sentences to gather information.
For each sentence, it comments, whether it contained compatible new information, contradicting information, or no new information.

For example

```
> if Prudence loathed Berti then Fiona loathed Berti
That's interesting.

> Prudence loathed Berti or Chester loathed Berti
That's interesting.

> it is not the case that Chester loathed Berti
That's interesting.

> Fiona loathed Berti
That's obvious.

> it is not the case that Prudence loathed Berti
That doesn't make any sense!
```

In the beginning, new information is gathered (*That's interesting.*).
From the 2nd and 3rd sentence it is deduced that *Prudence loathed Berti*,
so when we state the opposite in the end, it's a contradiction (*That doesn't make any sense!*).
From the first sentence it can also be deduced that *Fiona loathed Berti*,
so when we state this, it's already known (*That's obvious.*).


To understand how the script works, please take a look at the comments in the script (`examples/Example1.scala`).
