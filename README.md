

# FastrApp-Android

There are 795,000 strokes a year, leading to 140,000 deaths and costing the healthcare system $34 billion a year. With each minute in between the onset of the stroke and treatment of the stroke, 1.9 million neurons are lost, leading to higher mortality and worse outcomes.  FASTr is an Android app that takes the FAST criteria used to triage patients in the emergency department and brings this screening device into high risk patients' daily lives. 


## Installation

### IDE setup

We recommend Android Studio.

### Steps

1) Clone repository

2) Import project to Android Studio

3) Run Build > Clean Project, Build > Rebuild Project

4) Run 'app' (play button near top of IDE) to start emulator

**Note, the camera and microphone will only work on real Android device, not the emulator. If you own an android device, you may connect to that and select it from the list of devices in Android Studio.

## Tech Stack and Dependencies

We built an Android app with Kotlin.

No server required! :smile:


### Detecting Facial Droop

For detecting facial droop, we use the Google's Firebase MLKit:

https://firebase.google.com/docs/ml-kit/detect-faces

And for our baseline model we borrow from Foong et. al

`Droopy Mouth Detection Model in Stroke Warning`

https://ieeexplore.ieee.org/document/7783286

### Detecting Slurred Speech

For detecting slurred speech, we use Word Error Rate (https://www.wikiwand.com/en/Word_error_rate) as a baseline. We utilize this library:

https://github.com/romanows/WordSequenceAligner

## Assumptions

We assume we can improve our models for facial droop detection and slurred speech detection.

## Future Improvements and Directions

### Application

Integration with Apple HealthKit (and building an iOS app) would enable more passive monitoring of facial droop every time someone opens their phone.

We recognize the opportunity for integrating with different IoT devices for better ambient monitoring.

### Models

Our models for detecting facial droop and slurred speech are areas for improvement.

  
We've created baselines in both areas for a minimal viable product.

**For measuring any model improvements, we need to gather a test set!**

That in and of itself we recognize as a large task. 

#### Improving Facial Droop Detection

- pre-processing of faces - orientation, cropping, adjusting for light, etc.

- machine learning on top of certain facial landmark feature - we would need a massive training data set full of regulated medical data, and this is a non-direction for us at the moment


#### Improving Slurred Speech Detection

Literature review of latest research. Some promising papers:

- `Learning to detect dysarthria from raw speech` https://arxiv.org/abs/1811.11101 Millet, Zeghidour

- `OVERVIEW OF ANALYSIS AND CLASSIFICATION OF STUTTERED
SPEECH`  http://pep.ijieee.org.in/journal_pdf/11-273-147100198180-86.pdf Manjula, Kumar

## Contributing

  

### Install dev tools

You need to install a few tools to contribute this repository,

which can be done via `make setup`.

  

### Useful make commands

```

format Format kotlin code

check-format Print a diff of formatting changes required for any code change

setup Setup local environment and dev tools

help Show help

```
