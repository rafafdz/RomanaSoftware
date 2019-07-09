from PyQt5 import QtCore, QtGui, QtWidgets, uic
from app_style import STYLE

import configparser
import os
import sys

CONFIG_FILENAME = "romana_admin.config"
DEFAULT_CONFIG = {
    "CARD_PORT" : "COM3",
    "ADMIN_CARD" : "INSRTCRD",
    "CARDS_PATH" : "cards.db",
    "INFO_PATH" : "card_info.json",
    "COMPANY NAME" : "DEFAULT"
}

config = configparser.ConfigParser()

def generate_config():
    config["DEFAULT"] = DEFAULT_CONFIG
    config["User"] = {}
    with open(CONFIG_FILENAME, "w", encoding="utf-8") as file:
        config.write(file)

try:
    config.read(CONFIG_FILENAME)
except configparser.MissingSectionHeaderError as ex:
    # Bad Format Detected
    print("Corrupt configuration File")
    generate_config()
        
if not config.sections():
    generate_config()
    
    
class AdminInterface(QtWidgets.QWidget):
    def __init__(self,):
        super().__init__()
        uic.loadUi('uis/main.ui', self)
        self.register_btn.clicked.connect(MainInterface().change_register)
        self.info_btn.clicked.connect(MainInterface().change_info)
        self.charge_btn.clicked.connect(MainInterface().change_charge)
    
class UseCard(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        self.client_card = True
        uic.loadUi('uis/use_card.ui', self)
        self.cancel_btn.clicked.connect(MainInterface().change_main)
        
    def set_client_card(self):
        self.client_card = True
        self.user_type_lbl = "CLIENTE"
        
    def set_admin_card(self):
        self.self.client_card = False
        self.user_type_lbl = "ADMINISTRADOR"

class NotRegistered(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/not_registered.ui', self)
        
class AlreadyUsed(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/already_used.ui', self)
        
class ConfirmRegister(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/confirm_register.ui', self)
        
class RegisterOk(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/register_ok.ui', self)
        
class ReaderError(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/reader_error.ui', self)
        
class ChargeForm(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/charge_form.ui', self)
        
class ChargeOk(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/charge_ok.ui', self)
        
class NotDetected(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/not_detected.ui', self)
        self.retry_btn.clicked.connect(MainInterface().change_use_card)
        self.exit_btn.clicked.connect(MainInterface().change_main)
        
class ClientInfo(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/client_info.ui', self)
        self.exit_btn.clicked.connect(MainInterface().change_main)


class MainInterface():
    # Singleton Pattern Implementation. Be aware that generates tight coupling
    
    instance = None
    
    class __MainInterface(QtWidgets.QWidget):
        def __init__(self):
            super().__init__()
            
        def init_elements(self):
            self.state = None
            self.setStyleSheet(STYLE)
            self.title_lbl = QtWidgets.QLabel()
            self.title_lbl.setAlignment(QtCore.Qt.AlignCenter)
            self.stack = QtWidgets.QStackedWidget()
            vbox = QtWidgets.QVBoxLayout()
            vbox.addWidget(self.title_lbl)
            vbox.addWidget(self.stack)
            self.setLayout(vbox)
             
            self.main_interface = AdminInterface()
            self.use_card = UseCard()
            self.not_registered = NotRegistered()
            self.already_used = AlreadyUsed()
            self.confirm_register = ConfirmRegister()
            self.register_ok = RegisterOk()
            self.reader_error = ReaderError()
            self.charge_form = ChargeForm()
            self.charge_ok = ChargeOk()
            self.not_detected = NotDetected()
            self.client_info = ClientInfo()
            
            self.stack.addWidget(self.main_interface)
            self.stack.addWidget(self.use_card)
            self.stack.addWidget(self.not_registered)
            self.stack.addWidget(self.already_used)
            self.stack.addWidget(self.confirm_register)
            self.stack.addWidget(self.register_ok)
            self.stack.addWidget(self.reader_error)
            self.stack.addWidget(self.charge_form)
            self.stack.addWidget(self.charge_ok)
            self.stack.addWidget(self.not_detected)
            self.stack.addWidget(self.client_info)
            
            self.change_main()
            self.card_timer = QtCore.QTimer()
            self.card_timer.timeout.connect(self.change_not_detected)
            self.card_timer.setSingleShot(True)
            
            
        def set_title(self, title):
            self.title_lbl.setText(title)
            
        def change_card_client(self):
            self.use_card.set_client_card()
            self.change_use_card()
            
        def change_use_card(self):
            self.stack.setCurrentWidget(self.use_card)
            self.start_card_timeout()
            
        def change_register(self):
            self.set_title("Registro")
            self.change_card_client()
            
        def change_charge(self):
            self.set_title("Recargar Tarjeta")
            self.change_card_client()
        
        def change_info(self):
            self.set_title("Ver Información")
            self.change_card_client()
            
        def change_main(self):
            self.set_title("Romana")
            self.stack.setCurrentWidget(self.main_interface)
            
        def change_not_detected(self):
            if self.stack.currentWidget() == self.use_card:
                self.stack.setCurrentWidget(self.not_detected)
            
        def start_card_timeout(self):
            # Automatically stops and restarts timer
            self.card_timer.start(20000)
        
    def __new__(cls):
        if not MainInterface.instance:
            MainInterface.instance = MainInterface.__MainInterface()
            MainInterface.instance.init_elements()
        return MainInterface.instance
        
    def __getattr__(self, name):
        return getattr(self.instance, name)

    def __setattr__(self, name, value):
        return setattr(self.instance, name, value)

if __name__ == "__main__":
    app = QtWidgets.QApplication(sys.argv)
    ui = MainInterface()
    ui.show()
    sys.exit(app.exec_())

