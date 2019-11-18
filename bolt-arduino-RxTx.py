from boltiot import Bolt
from time import sleep
import json
from telgram_credentials.conf_telegram import *
import subprocess, requests, logging

api_key = "9555ccc7-de9e-4493-bf4d-bca28cd5d96d"
device_id = "BOLT3848004"
mb = Bolt(api_key, device_id) 

def send_telegram_message(message):
    """Sends message via Telegram"""
    url = "https://api.telegram.org/" + telegram_bot_id + "/sendMessage"
    data = {
        "chat_id": telegram_chat_id,
        "text": message
    }
    try:
        response = requests.request(
            "GET",
            url,
            params=data
        )
        print("This is the Telegram response")
        print(response.text)
        telegram_data = json.loads(response.text)
        return telegram_data["ok"]
    except Exception as e:
        print("An error occurred in sending the alert message via Telegram")
        print(e)
        return False


response = mb.serialBegin('9600')
while True:
    response1 = mb.serialRead('10') 
    dread = mb.digitalRead("1")
    data = json.loads(response1)
    dread1 = json.loads(dread)
    print(dread)
    
    if dread1["value"] == '1' or data["value"] == '1':
        
        # creating a subprocess to take images from ov7670 camera module
        command = '''C:
cd/
cd \"Program Files (x86)\"\\Java\\jdk1.8.0_221\\bin
java code.SimpleRead
'''
        pro = subprocess.Popen('cmd.exe', stdin=subprocess.PIPE, shell=True)
        _, _ = pro.communicate(command.encode("utf-8"))
        print("Done!!")

        # To fetch the image from images folder and send it via telegram 

        message = """Alert! Someone is near the door!!!
Click on the following link to display a message.
https://cloud.boltiot.com/control?name=BOLT3848004"""

        telegram_status = send_telegram_message(message)
        print("This is the Telegram status:", telegram_status)
   
        sleep(10)
    sleep(1)
