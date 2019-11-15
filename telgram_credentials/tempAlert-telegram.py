import requests                 # for making HTTP requests
import json                     # library for handling JSON data
import time                     # module for sleep operation

from boltiot import Bolt        # importing Bolt from boltiot module
import conf_telegram                     # config file

mybolt = Bolt(conf_telegram.bolt_api_key, conf_telegram.device_id)

def get_sensor_value(pin):
    """Returns the sensor value. Returns -999 if request fails"""
    try:
        response = mybolt.analogRead(pin)
        data = json.loads(response)
        if data["success"] != 1:
            print("Request not successfull")
            print("This is the response->", data)
            return -999
        sensor_value = int(data["value"])
        return sensor_value
    except Exception as e:
        print("Something went wrong when returning the sensor value")
        print(e)
        return -999


def send_telegram_message(message):
    """Sends message via Telegram"""
    url = "https://api.telegram.org/" + conf_telegram.telegram_bot_id + "/sendMessage"
    data = {
        "chat_id": conf_telegram.telegram_chat_id,
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
    # Step 1
    sensor_value = get_sensor_value("A0")    
    print("The current sensor value is:", sensor_value)
    
    # Step 2
    if sensor_value == -999:
        print("Request was unsuccessfull. Skipping.")
        time.sleep(10)
        continue
    
    # Step 3
    if sensor_value >= conf_telegram.threshold:
        print("Sensor value has exceeded threshold")
        message = "Alert! Sensor value has exceeded the threshold value. The current temperature is " + str(sensor_value * 100 // 1024) + " degree C"
        telegram_status = send_telegram_message(message)
        print("This is the Telegram status:", telegram_status)

    # Step 4
    print()
    time.sleep(10)