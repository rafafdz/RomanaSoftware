from PyQt5 import QtCore, QtGui, QtWidgets, uic
from PyQt5.QtCore import QRegExp
from PyQt5.QtGui import QRegExpValidator
from config import Config
from utilities import save_new_user, user_info, user_balance, add_balance
import sys
import os
sys.path.append(os.path.join(os.path.dirname(os.path.abspath(__file__)), "panels"))

from admin_interface import AdminInterface
from register_form import RegisterForm
from use_card import UseCard
from not_registered import NotRegistered
from incorrect_admin import IncorrectAdmin
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
        self.current_section = None
        self.current_card = None
        self.current_name = None
        self.current_rut = None
        self.current_company = None
        self.extra_balance = None        
    
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
        self.incorrect_admin  = IncorrectAdmin(self)
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
        self.stack.addWidget(self.incorrect_admin)
        self.stack.addWidget(self.already_used)
        self.stack.addWidget(self.confirm_register)
        self.stack.addWidget(self.register_ok)
        self.stack.addWidget(self.reader_error)
        self.stack.addWidget(self.charge_form)
        self.stack.addWidget(self.charge_ok)
        self.stack.addWidget(self.not_detected)
        self.stack.addWidget(self.client_info)
        
        self.change_main()
        
        # Developing porpouses
        #self.stack.setCurrentWidget(self.charge_form)
        
        
    def set_title(self, title):
        self.title_lbl.setText(title)
        
    def get_current_panel(self):
        return self.stack.currentWidget()
        
    def change_card_client(self):
        self.use_card.set_client_card()
        self.change_use_card()
        
    def change_card_admin(self):
        self.use_card.set_admin_card()
        self.change_use_card()
        
    def change_use_card(self):
        self.stack.setCurrentWidget(self.use_card)
        self.use_card.start_reading()
        
    def change_register(self):
        self.current_section = "register"
        self.set_title("Registro")
        self.change_card_client()
        
    def change_charge(self):
        self.current_section = "reload" # Dirty! use enumaretors
        self.set_title("Recargar Tarjeta")
        self.change_card_client()
    
    def change_info(self):
        self.current_section = "info"
        self.set_title("Ver Información")
        self.change_card_client()
        
    def change_main(self):
        self.set_title("Romana")
        self.current_section = None
        self.stack.setCurrentWidget(self.main_interface)
        
    def change_card_timeout(self):
        if self.get_current_panel() == self.use_card:
            self.stack.setCurrentWidget(self.not_detected)   
        
    def change_reader_error(self):
        if self.get_current_panel() == self.use_card:
            self.stack.setCurrentWidget(self.reader_error) 

    def change_confirm_register(self):
        self.confirm_register.card_lbl.setText(self.current_card)
        self.confirm_register.name_lbl.setText(self.current_name)
        self.stack.setCurrentWidget(self.confirm_register)
        
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
        self.extra_balance = None
        
    def card_read_action(self, card):
        print("Card Detected", card)
        
        if self.get_current_panel() != self.use_card:
            return
         
        if self.use_card.client_card:
            self.current_card = card
            if self.current_section in ("reload", "info") and not user_info(card):
                self.stack.setCurrentWidget(self.not_registered)
                return
            
            if self.current_section == "register":
                if user_info(self.current_card):
                    self.stack.setCurrentWidget(self.already_used)
                    return
                
                self.stack.setCurrentWidget(self.register_form)
                       
            elif self.current_section == "reload":
                user_data = user_info(self.current_card)
                balance = user_balance(self.current_card)
                
                self.charge_form.set_name(user_data["name"])
                self.charge_form.set_balance(balance)
                
                self.stack.setCurrentWidget(self.charge_form)
            
            elif self.current_section == "info":
                self.change_card_admin()
        
        else:
            if card != Config().get_conf("ADMIN_CARD"):
                self.stack.setCurrentWidget(self.incorrect_admin)
                return
            
            if self.current_section == "register":
                self.register_action()
            
            elif self.current_section == "reload":
                add_balance(self.current_card, self.extra_balance)
                new_balance = user_balance(self.current_card)
                
                self.charge_ok.set_balance(new_balance)
                self.stack.setCurrentWidget(self.charge_ok)            
            
            elif self.current_section == "info":
                
                info = list(user_info(self.current_card).values())
                balance = user_balance(self.current_card)
                
                balance = balance if balance is not None else ""
                
                args = info + [self.current_card, balance]
                
                self.client_info.set_user_info(*args)
                self.stack.setCurrentWidget(self.client_info)
                
    def add_balance_action(self, balance):
        self.extra_balance = balance
        self.change_card_admin()
       
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
