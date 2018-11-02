# FRC2019
1745s 2019 Robot Code.
|  master  |
| -------- |
| ![master build](https://travis-ci.org/Pearcerobotics/FRC2019.svg?branch=master) |
# Installation
## Visual Studio Code
Visual Studio Code is a lightweight free to use platform for software development. It supports Java and C++ development and with the supplied plugins you can develop FRC programs. Starting with the 2019 FRC season, VS Code will be the Integrated Development Environment that is supported. To get VS Code for your platform navigate to: https://code.visualstudio.com/download and select the version that matches your development OS. In this example, we'll install the Mac version of VS Code.


Choose the version for your hardware and install it by following the on-screen instructions which may vary slightly by platform.

Note: VS Code version 1.25 or later is required to work properly with the WPILib plugin.

Windows
For Windows, run the downloaded executable and follow the on-screen directions to complete the installation.


###Build
_Note: *nix users may have to `chmod +x gradlew` to give the executable the right permissions
- ```./gradlew build``` will build your Robot Code
- ```./gradlew deploy``` will build and deploy your code.
- ```./gradlew riolog``` will display the RoboRIO console output on your computer (run with `-Pfakeds` if you don't have a driverstation connected).

### Extentions
#### WPILib 
Download the WPILib VS Code extension
For the Alpha release, the WPILib VS Code extension is not part of the VS Code marketplace and it has to be downloaded and installed manually.

The download can be found at: https://github.com/wpilibsuite/vscode-wpilib/releases/tag/v2019.0.0-alpha-4

Click on the latest alpha file and you will be prompted to download the .vsix file.

Note: The Alpha files are intended for the public alpha and use the 2018 libraries and roboRIO image. The Beta files are intended for the closed Beta and require the 2019 roboRIO image.

Install the WPILib VSstat Code extension
To install the WPILib extension (1) click on the ... button above the extensions search bar. (2) Select "Install from VSIX...".

Select the vsix file the was downloaded in the previous step and click "Install" to install the extension. After the installation finishes,  click "Reload Now" to restart VS Code.

#### TSLint
#### JUnit
#### HTML/CSS Tools

## Librarys
### CTRE Phoenix
### REV Tools

## Addon Tools
### GRIP
### FRC Dashboard
Follow the instructions on the FRC Dashboard Github Page for instalation https://github.com/FRCDashboard/FRCDashboard
### Spark Tools

# Building Code
## Building Code For the Robot
## Building Code For the Dashboard
## Building Code For Vision

# Deploying Code
## Building Code For the Robot
## Building Code For the Dashboard
## Building Code For Vision

# Pull Request Guidlines
## Code Style Guidelines
## Code Unit Testing Guidelines
## Pull Request Submitions in Github Projects

# Robot Functionality 
