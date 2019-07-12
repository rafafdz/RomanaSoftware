import json
from config import Config
import os


CONFIG = Config()

def save_new_user(card_id, name, rut, company):
    new_info = {"name" : name, "rut" : rut, "company" : company}
    
    current_info = read_info()
    current_info[card_id] = new_info 
    
    path = CONFIG.get_conf("INFO_PATH")
    with open(path, "w", encoding="utf-8") as file:
        json.dump(current_info, file)
    
    
def read_info():
    path = CONFIG.get_conf("INFO_PATH")
    
    if not os.path.exists(path):
        print("Generando nuevo archivo de información")
        file = open(path, "w")
        file.write("{}\n")
        file.close()
    
    with open(path, encoding="utf-8") as file:
        return json.load(file)
    
    
def user_info(card_id):
    current_info = read_info()
    if card_id not in current_info:
        return None
    
    return current_info[card_id]    
    
if __name__ == "__main__":
    save_new_user("test", "hola", "kek", "chao")