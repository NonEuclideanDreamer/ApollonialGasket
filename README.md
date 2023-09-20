# ApollonialGasket
Explore Apollonial Gaskets or run Cellular Automata on them

Classes to be put together in a java project
The main classes are Draw.java and Automaton.java

Draw.java: Draw Apollonial gaskets.
  There are four modes for the animation, that can be chosen by uncommenting the desired mode:
  -warp: change initial curvatures from frame to frame, details need to be set in the main method
  -it: iterate a gasket: in each step all circles touching 3 already existing circles are created
  -int: Go through cases of all-integer Gaskets. Details need to be set in main method
  -zoom: zoom into a gasket, smaller circles are created as you go
  There are 2 modes of coloring:
  -gradient: The first four circles get the colors specified in Pantheon.java, the other circles are interpolated
  -pastellprimes: (for integer curvatures: the color is specified by the number of 2s, 3s and 5s in the prime factorization of the curvature

Automaton.java: Run an Automaton on the Gasket, that in turn warps the Gasket
    I only coded up one initial state, and went for very small state changes for getting smooth animations, one could try a lot more I'd wager.

Circle.java: Class describing circles through location (of center) and radius/curvature.
    Note: the attribute r is double valued, whereas c is a int. This way I can do exact calculations in all-integer-curvature gaskets using the same circle class as in spectrum-valued curvature cases

Pantheon.java: Class describing an Apollonial Gasket defined from 3 initial curvatures. 
      I called Apollonial Gaskets "pantheons" when first exploring them a few years back, likening the entirety of all the circles fitting together to all the major and minor gods of an ancient religion. 

If one wants to write the curvature of the circles on them, one needs the class "Writing.java" from the DynamicalSystems-Repository as well as the number-bmp-files in there.

Feel free to ask if there are questions, e.g. on my discord: 
https://discord.gg/etCwsXXZY
