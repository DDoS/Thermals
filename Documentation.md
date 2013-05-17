## Argument Types

### Integer

Represents an integer (whole) number with a value range of about -2 to 2 billion.  
Decimal places are not accepted.

### Location

Represents a 2D location in a world.

Format:

`[(integer) x],[(integer) z] OR here OR there`

`here` represents the player's current location (x and z).  
`there` represents the player's target location (x and z of the block being looked at, up to 500 blocks away).  
`here` and `there` can only be used in game.

Example:

`-32,76`

- - -

## Command Usage

Clears the heat value at the given location.  
`/th clear [(location) location]`

Clears all of the heat values in a square of given radius, with the location being the middle.  
`/th clear [(location) middle] [(integer) radius]`

Clears all of the heat values in the area included between the first and second locations.  
`/th clear [(location) from] [(location) to]`

Clears all of the heat values. This action is irreversible!  
`/th clear all`

Gets the heat value at the given location.  
`/th get [(location) location]`

Sets the heat value at the given location.  
 `/th set [(location) location] [(integer) heat]`

Sets all of the heat values in a square of given radius, with the location being the middle, to the given heat.  
`/th set [(location) middle] [(integer) radius] [(integer) heat]`

Sets all of the heat values in the area included between the first and second locations to the given heat.  
`/th set [(location) from] [(location) to] [(integer) heat]`

Generates a heat map of all of the heat values in a square of given radius, with the location being the middle, and saves it as PNG in the plugin data folder, overriding any existing file with a conflicting name. The file name should not include the extension.  
`/th gen [(location) middle] [(integer) radius] [(string) file name]`

Generates a heat map of all of the heat values in the area included between the first and second locations and saves it as PNG in the plugin data folder, overriding any existing file with a conflicting name. The file name should not include the extension.  
`/th gen [(location) from] [(location) to] [(string) file name]`

Attemps to establish a connection to the database.  
`/th connect`
