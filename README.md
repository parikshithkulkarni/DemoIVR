# DemoIVR
Demonstrating automation of IVR testing (open speech and DTMF)

File structure:
- DemoIVR is main, inside of it we make a call with an Android, then do the IVR test. testIVRFlowViaT2S does it via text to speech and audio injection. testIVRFlowViaDTMF controls the flow via key presses. For each step the response is recorded and translated from speech to text, later compared to the required value.

- VoiceServices is where the IBM Watson integration happens (speech to text, text to speech)

- PerfectoUtils includes helping functions such as audio inject, androidCall etc.


Getting started with the project:

- You need to define the device in TestNG

- In TestNG configuration you need to define several environment variables, see environment_variables.png




