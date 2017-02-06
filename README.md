# **Press SPACE to Wave** - Global Game Jam 2017

## Team Members
* Dominic Mortlock
* Skanda Narendra Bhargav
* Zac Patel
* Ben Thomas

##Download
* [Desktop Download Page - TBD](https://google.com)
* [Google Play Listing - TBD](https://google.com)

## **About**

"Press SPACE to Wave" was created during the 2017 Global Game Jam at Google Launchpad in San Francisco. The theme for the game jam was "waves". The team was excited about the prospect of waves as a form of communication between humans and thought that a game about propogating human waves would be a fun project. The game that resulted is a 2D top down local multiplayer game created using the LibGDX framework. 

### How to Play 

The players use the space bar on desktop (any part of screen on mobile) to "wave" down neutral pedestrians. Each pedestrian can only wave "one" time. To win the game connect with every pedestrian in the city!

### How to "Run"

For Desktop machine must have java installed. EXE run file available for Windows. JAR run file available for Mac and Linux users (and Windows). Hosted on [itch.io - To be Added](https://google.com).

For Android the game can be found on the [Google Play - To Be Added](https://google.com) app store and downloaded to your Android device.

## How it was programmed

#### Tools
Press SPACE to Wave was programmed in Java through the **LibGDX** framework. The **Box2D** physics extension was utilized to create physics bodies the represent the pedestrians, buildings, and wave projectiles. This collision library allowed us to propogate the waves between the pedestrians. 

**Adobe Photoshop** was used to draw all the art assets. 

#### Design
TBD

#### Directory Structure

The *"Desktop"*, *"Android"*, and *"HTML"* folders hold the gradle build profiles and launcher classes for their respective platforms. All of the games art and sound assets are stored and referenced in the Android *"Assets"* folder. The *"src"* folder in the *"core"* folder holds the game logic files. "Press SPACE to Wave" uses the "screen" LibGDX feature so each java game file corresponds to a game screen. "Press SPACE to Wave" has 2 screens. The *"gameScreen.java"* file holds the main menu screen that launches at start of game and then releases that menu for the actual gameplay. The *"gameOverScreen.java"* file is triggered when the escape button is pressed and gives you your score. The *"TeamWave.java"* file is the game class that LibGDX defaults to to launch the game. In PSTW the game class simply establishes the game object and points to the game screen to start the game. 

#### Contributing

Anyone is welcome to re-use the code used in this project.

#### References

* [LibGDX](https://libgdx.badlogicgames.com/)
* [LibGDX WIKI](https://github.com/libgdx/libgdx/wiki)
* [GamesFromScratch LibGDX Tutorial Series](http://www.gamefromscratch.com/page/LibGDX-Tutorial-series.aspx)
* [Box2D LibGDX WIKI](https://github.com/libgdx/libgdx/wiki/Box2d)
* [Freesound.org](http://freesound.org/)
* [Android Studio](https://developer.android.com/studio/index.html)

#### Contact Me

For any questions please email us at _bthomas2622@gmail.com_, _skanda.narendrabhargav@sjsu.edu_, _dom.mortlock@gmail.com_, _zpatel@berkeley.edu_

#### License

The content of this repository is not licensed. 