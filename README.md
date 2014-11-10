Consists of two applications, a mobile application for a smartphone, and a wearable application for an android wear compatible device, such as a smartwatch

This has been developed and tested with a Google Nexus 5 smartphone and Moto 360 watch, but should be compatible with any smartphone running android KitKat or newer and Android Wear compatible device.

When Data Collection is enabled, approximately every 15 minutes the smartphone sends a message to the watch to collect a sample of sensor data. Upon receiving the message, the watch will begin listening for changes on all of its sensors, including body sensors, for 30 seconds. After the 30 seconds, a single reading from each sensor will be sent back to the smartphone. The recorded reading will be the most accurate reading during the listening window. Since most sensors achieve maximum accuracy rather quickly, each sensor will opportunistically stop listening once it finds a reading of maximum accuracy in order to conserve power. Some sensors, such as heart rate, can be slow to reach accurate readings, thus the need for the 30 second window in the first place.

When the smartphone receives a new reading from the watch, it appends one extra label to the data, a boolean value for whether or not you are sleeping at the time of data collection. This is manually determined by the "Sleeping" switch in the smartphone app. It then serializes the collection of readings, a timestamp, and the sleeping label into JSON format, and records that JSON object as a single line in a file named `wearable_data.txt` on the smartphone located in the phone's Documents directory. Retrieving this file can be done through Android File Transfer, adb, or any other application that allows access to the phone's filesystem.
