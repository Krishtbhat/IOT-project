# IOT-project
A smart door system for the deaf and hard of hearing which in extension can also be used for the public.

# Steps to setup the project
1. Make the connections between various components as shown in the circuit diagram in the circuit-diagram folder.
2. Click on the following link to setup the camera with the arduino https://create.arduino.cc/projecthub/techmirtz/visual-capturing-with-ov7670-on-arduino-069ebb .
3. Upload the arduino program SimpleRead.ino present in the SimpleRead folder to the arduino connected to the camera.
4. Upload the arduino program ultrasonic-serial.ino present in the ultrasonic-serial.ino folder to the other arduino.
5. Once the arduino program ultrasonic-serial.ino is running in the arduino, connect the arduino to the laptop and run the python program bolt-arduino-RxTx.py which clicks and sends the image of the visitor.

