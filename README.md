# Solibri rule API examples

## Build instructions

Assumes a standard Solibri Model Checker (SMC) installation. 
There's a `platform-mac` profile for Mac users.

Build instructions: Run maven with
```
mvn clean install
```

## Usage in Solibri Office
 
- Run Solibri Office. 
- Switch to the rule manager. If correctly installed, 
you will find a new rule set. 

## Examples
### Top surface slope check

Will check the inclination of top surfaces by a given minimum threshold
given as a percentage of slope, where 0 deg is a horizontal plane and 100 percent refers to
a 45 deg inclination against the up-pointing Z-axis.

## Licenses

Apache-2.0