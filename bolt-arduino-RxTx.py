from boltiot import boltiot
import telegram
import json, time
from telgram_credentials.conf_telegram import *
import subprocess, requests

api_key = "9555ccc7-de9e-4493-bf4d-bca28cd5d96d"
device_id = "BOLT3848004"
mb = Bolt(api_key, device_id) 

bot = telegram.Bot(token = "1031896771:AAFlMFR1Q7Z8kdxD9M4MNiPaUZkMBl7hv4I")

def send_telegram_message(message):
    """Sends message via Telegram"""
    global bot
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
        bot.send_photo(chat_id = "@alerts_boltIOT19", photo = open("./images/1.bmp", "rb"))
        print("This is the Telegram response")
        print(response.text)
        telegram_data = json.loads(response.text)
        return telegram_data["ok"]
    except Exception as e:
        print("An error occurred in sending the alert message via Telegram")
        print(e)
        return False


response = mb.serialBegin('9600')
with open("fnum.txt", 'r') as f:
    imageNum = len(f.read().split())
while True:
    # response1 = mb.serialRead('10') 
    dread = mb.digitalRead("1")
    # data = json.loads(response1)
    dread1 = json.loads(dread)
    print(dread)
    
    if dread1["value"] == '1':
        with open("image-count.txt", "r") as f:
            image_count = f.read()
        # creating a subprocess to take images from ov7670 camera module
        command = '''C:
cd/
cd "Program Files (x86)"\\Java\\jdk1.8.0_231\\bin
java code.SimpleRead '''+ image_count +'''
'''

        pro = subprocess.Popen('cmd.exe', stdin=subprocess.PIPE, shell=True)
        _, _ = pro.communicate(command.encode("utf-8"))
        print("Done!!")
        image_count = int(image_count) + 1
        with open("image-count.txt", "w") as f:
            if image_count == 20:
                f.write("0")
            else:
                f.write(str(image_count))
        # To fetch the image from images folder and send it via telegram 
        
        message = """Alert! Someone is near the door!!!
Click on the following link to display a message.
https://cloud.boltiot.com/control?name=BOLT3848004"""

        telegram_status = send_telegram_message(message)
        print("This is the Telegram status:", telegram_status)
   
        start = time.perf_counter()
        while(start + 10.0 > time.perf_counter() and json.loads(mb.digitalRead('1'))["value"] != '1'):
            pass
    time.sleep(1)
