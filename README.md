# Picross Editor

**Keywords**: Nonogram, Griddlers, Illust Logic, Picture Logic, Picross

## Download

[picross-editor-0.1.0.jar](https://github.com/atmarksharp/atmarksharp.github.io/raw/master/picross-editor/picross-editor-0.1.0.jar)

[picross-editor-0.1.0-source.zip](https://github.com/atmarksharp/atmarksharp.github.io/raw/master/picross-editor/picross-editor-0.1.0-source.zip)

## Run Samples

### Dependencies

- Java Development Kit (JDK) **version 6** (or higher)

### Instructions

1) Download **jar file** and **Source**

2) Defrost source onto **~/Downloads**

2) Open the **Terminal.app**

2) Type below


```sh
cd ~/Downloads/picross-editor-0.1.0
java -jar ~/Downloads/picross-editor-0.1.0.jar examples/e.txt
```

## Status

_**[ Bleeding Edge ]**_

## Build from Source

Dependencies:

- Ant 1.9.4 (or higher)
- Ivy 2.3.0 (or higher)

```sh
git clone https://github.com/atmarksharp/picross-editor
cd picross-editor
ant jar
```

### Test

Dependencies:

- tesseract 3.02.02 (or higher) (ex: brew install tesseract)

```sh
ant test
```


