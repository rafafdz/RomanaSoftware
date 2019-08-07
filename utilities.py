import json
from config import Config
import os

# TODO: Eliminate duplicated code

CONFIG = Config()

def save_new_user(card_id, name, rut, company):
    new_info = {"name" : name, "rut" : rut, "company" : company}
    
    current_info = read_info()
    current_info[card_id] = new_info
    path = CONFIG.get_conf("INFO_PATH")
    
    with open(path, "w", encoding="utf-8") as file:
        json.dump(current_info, file)
    
    path = CONFIG.get_conf("CARDS_PATH")
    current_data = read_balance()
    current_data[card_id] = 0
    
    with open(path, "w", encoding="utf-8") as file:
        json.dump(current_data, file)
    
    
def read_info():
    path = CONFIG.get_conf("INFO_PATH")
    
    if not os.path.exists(path):
        print("Generando nuevo archivo de información")
        file = open(path, "w")
        file.write("{}\n")
        file.close()
    
    with open(path, encoding="utf-8") as file:
        return json.load(file)
    
    
def read_balance():
    path = CONFIG.get_conf("CARDS_PATH")
    
    with open(path, encoding="utf-8") as file:
        return json.load(file)
    
def user_info(card_id):
    current_info = read_info()
    
    if card_id not in current_info:
        return None
    
    return current_info[card_id]

def user_balance(card_id):
    current_data = read_balance()
    
    if card_id not in current_data:
        return None
    
    return current_data[card_id]

def add_balance(card_id, money):
    """To Be used when the card is verified to exist"""    
    current_data = read_balance()
    current_data[card_id] = float(current_data[card_id]) + money
    
    path = CONFIG.get_conf("CARDS_PATH")
    with open(path, "w", encoding="utf-8") as file:
        json.dump(current_data, file)
    
if __name__ == "__main__":
    user_info("2323ddd")