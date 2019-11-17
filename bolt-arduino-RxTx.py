from boltiot import Bolt
from time import sleep
import json, telgram_credentials.conf_telegram
import subprocess, os, signal, requests, logging

api_key = "9555ccc7-de9e-4493-bf4d-bca28cd5d96d"
device_id = "BOLT3848004"
mb = Bolt(api_key, device_id) 

def send_telegram_message(message):
    """Sends message via Telegram"""
    url = "https://api.telegram.org/" + telgram_credentials.conf_telegram.telegram_bot_id + "/sendMessage"
    data = {
        "chat_id": telgram_credentials.conf_telegram.telegram_chat_id,
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

count = 0
response = mb.serialBegin('9600')
while True:
    count += 1
    response1 = mb.serialRead('10') 
    dread = mb.digitalRead("1")
    data = json.loads(response1)
    dread1 = json.loads(dread)
    print("From Rx: ", data["value"])
    print(dread)
    
    # if dread1["value"] == '1' or data["value"] == '1':
    if count % 3 == 0:
        
        # creating a subprocess to take images from ov7670
        command = '''
C:
cd \"Program Files (x86)\"\\Java\\jdk1.8.0_221\\bin\\code
java While
'''
        # proc = subprocess.Popen('cmd.exe', stdin=subprocess.PIPE, shell=True)
        # try:
        #     proc.communicate(command.encode("utf-8"), timeout=5)
        # except Exception as e:
        #     #subprocess.Popen("TASKKILL /F /PID {pid} /T".format(pid=process.pid), shell=True)
        #     print("OK")
        #     os.kill(proc.pid, signal.CTRL_C_EVENT)

        pro = subprocess.Popen("python while.py", stdin=subprocess.PIPE, shell=True)
        _, _ = pro.communicate()
        sleep(3)

        # to fetch the image from out folder 

        print("Done!!")
        message = """Alert! Someone is near the door!!!
Click on the following link to display a message.
https://cloud.boltiot.com/control?name=BOLT3848004"""

        
        # telegram_status = send_telegram_message(message)
        # print("This is the Telegram status:", telegram_status)
   
        sleep(10)
    sleep(1)
