## Introduction

Thermals is a Bukkit plugin for logging redstone events located in a world. The user can then request the generation
of heat maps for an area, showing the redstone activity. Higher activity means hotter regions, lower, cooler. No activity
is colorless, and the background is visible.

- - -

## Installation

Drop the Thermals.jar into your `plugins` folder. Start up the server to generate the `plugins/Thermals/`
folder and the `config.yml` configuration in it.

- - -

## Configuration

This will be explained section by section.

### Database connection information

    database:
        type: MySQL
        host: localhost
        port: '3306'
        name: minecraft
        user: root
        password: password

Change this information to match your database information.  
Here are the supported types:

* MySQL
* More to come!

Some databse types might not require all of the information under the database section.  
Here is the list of the necessary information for each type:

* MySQL: all of the information

### Logger

    logger:
        world: world
        delay: 30000
        run-threshold: -1
        suppress-info: false

These are the settings for the logger, which records the information to the database.
It will run periodically, processing all or a limited number of records, called "heats".

The first setting is the world. The logger will log all of the redstone change events in the specified world only.
An invalid world will lead to a partially loaded and not functional plugin.

You can change the delay between each run. It is milliseconds. The default is 30 seconds.

The run threshold is the maximum of heats to process during each run. -1 represents no maximum.
It is the recommended default.

You can suppress the logging to the console of the information for each run by changing the `suppress-info`
value to `true`.

### Generator

    generator:
        heat-bounds:
            min: 0
            max: 5000
        heat-gradient:
        - ==: Color
          RED: 0
          BLUE: 0
          GREEN: 0
       - ==: Color
          RED: 50
          BLUE: 150
          GREEN: 0
        - ==: Color
          RED: 200
          BLUE: 140
          GREEN: 15
        - ==: Color
          RED: 250
          BLUE: 0
          GREEN: 120
        - ==: Color
          RED: 250
          BLUE: 0
          GREEN: 200
        - ==: Color
          RED: 255
          BLUE: 255
          GREEN: 255
        background:
            color:
                ==: Color
                RED: 150
                BLUE: 150
                GREEN: 150
            grid:
                draw: true
                block-interval: 16
                line-color:
                    ==: Color
                    RED: 0
                    BLUE: 0
                    GREEN: 255
                coords:
                    draw: true
                    grid-line-interval: 3
                    point-color:
                        ==: Color
                        RED: 255
                        BLUE: 0
                        GREEN: 0
                    font:
                        color:
                            ==: Color
                            RED: 0
                            BLUE: 0
                            GREEN: 0
                        name: Myriad Pro
                        size: 10

The generator is a process responsible for generating the heat map images from the information stored in the
database.

The heat generator will ignore any values bellow the minimum bound. Any above the maximum will be treated as being equal
to this value. The minimum bound represents no heat, and the maximum, maximum heat

The heat gradient is used to map integer values to colors. It is ordered from coolest to hotest. Colors are expressed
in the standard RGB way, with each color component ranging from 0 to 255. You can add as many colors as you want to
the list. The default gradient is the thermographic one, with, from coolest to hotest: black, dark purple, redish pink,
orange, yellow and white.

The heat maps have a configurable background color, with optional grids and coordinates. The background color is, by
default, a dark grey. A green grid, with lines spaced 16 blocks apart is also drawn. Coordinates too are added every
3 grid lines, at intersections. These points of intersection will be marked in red. The font used is "Myriad Pro", size 10,
in black.

Heat maps are overlayed on the background, and might mask some of the grid and coordinates.

- - -

## Formatting key

This is the meaning of the formatting used in the documentation bellow.

`[(type) value]`

* User defined arguments are between brackets.
* The types for the arguments are between paranthesis.
* Floating words are constants, and shouldn't be altered.

- - -

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

There is no space after the coma separator!

### String

A simple chain of alphanumeric characters without any whitespace.

Example:

`string_example1`

- - -

## Command Usage

### Clearing

Clears the heat value at the given location.  
`/th clear [(location) location]`

Clears all of the heat values in a square of given radius, with the location being the middle.  
`/th clear [(location) middle] [(integer) radius]`

Clears all of the heat values in the area included between the first and second locations.  
`/th clear [(location) from] [(location) to]`

Clears all of the heat values. This action is irreversible!  
`/th clear all`

### Getting

Gets the heat value at the given location.  
`/th get [(location) location]`

### Setting

Sets the heat value at the given location.  
 `/th set [(location) location] [(integer) heat]`

Sets all of the heat values in a square of given radius, with the location being the middle, to the given heat.  
`/th set [(location) middle] [(integer) radius] [(integer) heat]`

Sets all of the heat values in the area included between the first and second locations to the given heat.  
`/th set [(location) from] [(location) to] [(integer) heat]`

### Generating

Generates a heat map of all of the heat values in a square of given radius, with the location being the middle, and saves it as PNG in the plugin data folder, overriding any existing file with a conflicting name. The file name should not include the extension.  
`/th gen [(location) middle] [(integer) radius] [(string) file name]`

Generates a heat map of all of the heat values in the area included between the first and second locations and saves it as PNG in the plugin data folder, overriding any existing file with a conflicting name. The file name should not include the extension.  
`/th gen [(location) from] [(location) to] [(string) file name]`

### Other

Attemps to establish a connection to the database.  
`/th connect`
