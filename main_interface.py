from PyQt5 import QtCore, QtGui, QtWidgets, uic
from PyQt5.QtCore import QRegExp
from PyQt5.QtGui import QRegExpValidator
from config import Config
from utilities import save_new_user
import sys
import os
sys.path.append(os.path.join(os.path.dirname(os.path.abspath(__file__)), "panels"))

from admin_interface import AdminInterface
from register_form import RegisterForm
from use_card import UseCard
from not_registered import NotRegistered
from already_used import AlreadyUsed
from confirm_register import ConfirmRegister
from register_ok import RegisterOk
from reader_error import ReaderError
from charge_form import ChargeForm
from charge_ok import ChargeOk
from not_detected import NotDetected
from client_info import ClientInfo                
    
class MainInterface(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("RomanaSoftware | Admin")
        self.init_elements()
        
    def _load_style(self):
        with open("stylesheet.css", encoding="utf-8") as file:
            style = file.read()
            self.setStyleSheet(style)
        
    def init_elements(self):
        self._load_style()
        self.current_card = "test"
        self.current_name = None
        self.current_rut = None
        self.current_company = None        
    
        self.title_lbl = QtWidgets.QLabel()
        self.title_lbl.setAlignment(QtCore.Qt.AlignCenter)
        self.stack = QtWidgets.QStackedWidget()
        vbox = QtWidgets.QVBoxLayout()
        vbox.addWidget(self.title_lbl)
        vbox.addWidget(self.stack)
        self.setLayout(vbox)
            
        self.main_interface   = AdminInterface(self)
        self.use_card         = UseCard(self)
        self.register_form    = RegisterForm(self)
        self.not_registered   = NotRegistered(self)
        self.already_used     = AlreadyUsed(self)
        self.confirm_register = ConfirmRegister(self)
        self.register_ok      = RegisterOk(self)
        self.reader_error     = ReaderError(self)
        self.charge_form      = ChargeForm(self)
        self.charge_ok        = ChargeOk(self)
        self.not_detected     = NotDetected(self)
        self.client_info      = ClientInfo(self)
        
        self.stack.addWidget(self.main_interface)
        self.stack.addWidget(self.use_card)
        self.stack.addWidget(self.register_form)
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
        
        # Developing porpouses
        self.stack.setCurrentWidget(self.register_form)
        
        
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
            
    def change_confirm_register(self):
        self.confirm_register.card_lbl.setText(self.current_card)
        self.confirm_register.name_lbl.setText(self.current_name)
        self.stack.setCurrentWidget(self.confirm_register)
        
    def start_card_timeout(self):
        # Automatically stops and restarts timer
        self.card_timer.start(20000)

    def set_user_info(self, name, rut, company):
        self.current_name = name
        self.current_rut = rut
        self.current_company = company
        
    def clear_and_exit(self):
        self.clear_user_info()
        self.change_main()
        
    def clear_user_info(self):
        self.current_card = None
        self.current_name = None
        self.current_rut = None
        self.current_company = None
        
    def register_action(self):
        save_new_user(self.current_card, self.current_name, 
                        self.current_rut, self.current_company)
        
        self.clear_user_info()
        self.stack.setCurrentWidget(self.register_ok)
        

if __name__ == "__main__":
    app = QtWidgets.QApplication(sys.argv)
    ui = MainInterface()
    ui.show()
    sys.exit(app.exec_())
