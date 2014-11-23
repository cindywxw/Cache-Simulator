# MESI Cache Simulator #

Java based MESI and MSI cache coherence simulator on a multi core system. It can be used to check the hit/miss rate of a certain cache configuration using a given trace file.

## Architecture ##
The simulator project is written in Java and divided into two main classes: The cache and the managing trace reader class. While all cache parameters and methods, such as the state update function are handled by the cache class, the trace reader maintains the set of processors and its traces, supervising the higher level cache coherence.

## Usage ##
The program takes in the following parameters to generate the results.

* Trace file name 
* Number of cores
* Protocol (MSI/MESI)
* Associativity
* Cache Size
* Cache Block Size

The provided trace file should contain 
* Action(0-fetch, 1-read, 2-write)
* 32 bit Address in hex

## Disclaimer ##

This is a school project geared towards a very specific use case and may need extensive modifying to be successsfully used. 
