# Jartin

Jartin is an open source abstract art generator, it uses user provided stencils to generate random abstract art.
Jartin is trivial to use and does not require any initial configuration to get started.

## Examples

Some nice examples of Jartin generated images:
![Example 1](http://i.imgur.com/gcwQrcm.jpg)
![Example 2](http://i.imgur.com/Tb0oHxR.jpg)
![Example 3](http://i.imgur.com/yvXT2or.jpg)

## Getting Started

You can build the app by Maven.

### Build for Windows based systems
```
mvn package -Pwin
```

As a result
```
{project.dir}/target/jartin/jartin
```
should then contain the fully compiled app.

### Build for Unix based systems
```
mvn package -Punix
```

As a result
```
{project.dir}/target/jartin/jartin
```
should then contain the fully compiled app.

## Resolve copyright problems ##

If Jartin stamps contain any of your brushes/stencils and you are not happy that they are being used here,
then please let me know and I'll remove them.


## License ##

Jartin is licensed under GNU GPLv3 [link](https://github.com/JoonasVali/jartin/blob/master/license.txt)