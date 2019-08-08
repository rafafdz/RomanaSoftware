from configparser import ConfigParser
import os


class Config():
    # Singleton Pattern
    FILENAME = "admin_config.ini"
    DEFAULT_CONFIG = {
        "CARD_PORT" : "COM3",
        "ADMIN_CARD" : "INSRTCRD",
        "CARDS_PATH" : "cards.db",
        "INFO_PATH" : "card_info.json",
        "COMPANY NAME" : "DEFAULT"
    }
    
    instance = None
    class __Config(ConfigParser):
        
        def __init__(self):
            super().__init__()
            try:
                self._load()
            except Exception as ex:
                print("Corrupt configuration File")
                os.rename(Config.FILENAME, f"{Config.FILENAME}.corrupt")
                self._generate_default
                return
        
            if not self.sections():
                self._generate_default()
        
        def _generate_default(self):
            data = {"DEFAULT" : Config.DEFAULT_CONFIG, "User" : {}}
            self.read_dict(data)
            
            with open(Config.FILENAME, "w", encoding="utf-8") as file:
                self.write(file)
                
        def _load(self):
            self.read(Config.FILENAME)
            
        def get_conf(self, param):
            return self["User"].get(param)
        
    def __new__(cls):
        if not Config.instance:
            Config.instance = Config.__Config()
        return Config.instance
    
    def __getattr__(self, name):
        return getattr(self.instance, name)

    def __setattr__(self, name, value):
        return setattr(self.instance, name, value)