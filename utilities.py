import json
from config import Config
import os

# TODO: Eliminate duplicated code

CONFIG = Config()


def _load_json(filename):
    with open(filename, encoding="utf-8") as file:
        return json.load(file)

def _save_json(filename, data):
    with open(filename, "w", encoding="utf-8") as file:
        json.dump(data, file, indent=4)
        
def _load_balance():
    path = CONFIG.get_conf("CARDS_PATH")
    return _load_json(path)

def _load_info():
    path = CONFIG.get_conf("INFO_PATH")
    return _load_json(path)

def _save_balance(cards_dict):
    path = CONFIG.get_conf("CARDS_PATH")
    _save_json(path, cards_dict)
    
def _save_info(info_dict):
    path = CONFIG.get_conf("INFO_PATH")
    _save_json(path, info_dict)

def save_new_user(card_id, name, rut, company):
    new_info = {"name" : name, "rut" : rut, "company" : company}
    
    if card_id == CONFIG.get_conf("ADMIN_CARD"):
        return
    
    current_info = _load_info()
    current_info[card_id] = new_info
    _save_info(current_info)
    
    current_data = _load_balance()
    current_data[card_id] = 0
    _save_balance(current_data)
    
    
def user_info(card_id):
    current_info = _load_info()
    
    if card_id not in current_info:
        return None
    
    return current_info[card_id]

def user_balance(card_id):
    current_data = _load_balance()
    
    if card_id not in current_data:
        return None
    
    return current_data[card_id]

def add_balance(card_id, money):
    """To Be used when the card is verified to exist"""    
    current_data = _load_balance()
    current_data[card_id] = float(current_data[card_id]) + money
    _save_balance(current_data)    
        
def hide_card(card_id):
    if len(card_id) < 4:
        return "X" * 8
    
    return card_id[:4] + "X" * 4
    
if __name__ == "__main__":
    user_info("2323ddd")