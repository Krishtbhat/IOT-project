from boltiot import Bolt
from time import sleep
import json, temp_alert_telegram.conf_telegram, requests

api_key = "9555ccc7-de9e-4493-bf4d-bca28cd5d96d"
device_id = "BOLT3848004"
mb = Bolt(api_key, device_id) 

def send_telegram_message(message):
    """Sends message via Telegram"""
    url = "https://api.telegram.org/" + temp_alert_telegram.conf_telegram.telegram_bot_id + "/sendMessage"
    data = {
        "chat_id": temp_alert_telegram.conf_telegram.telegram_chat_id,
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

while True:
    response = mb.serialBegin('9600')
    response1 = mb.serialRead('10') 
    dread = mb.digitalRead("1")
    data = json.loads(response1)
    dread1 = json.loads(dread)
    print("From Rx: ", data["value"])
    print(dread)
    
    if True:
        message = """Alert! Someone is near the door!!!
Click on the following link to display a message.
https://cloud.boltiot.com/control?name=BOLT3848004"""
        print(message)
        telegram_status = send_telegram_message(message)
        print("This is the Telegram status:", telegram_status)
        sleep(30)
    sleep(1)