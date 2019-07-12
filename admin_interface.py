from PyQt5 import QtCore, QtGui, QtWidgets, uic
from PyQt5.QtCore import QRegExp
from PyQt5.QtGui import QRegExpValidator
from app_style import STYLE
from config import Config
from utilities import save_new_user


import sys
import json

    
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
        
class RegisterForm(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/register_form.ui', self)
        regex_rut = QRegExp("\d{1,3}\.?\d{3}\.?\d{3}\-?[0-9kK]{1}")
        self.rut_entry.setMaxLength(13)
        self.rut_entry.setValidator(QRegExpValidator(regex_rut))
        self.rut_entry.editingFinished.connect(self._format_action)
        self.confirm_btn.clicked.connect(self._confirm_action)
        self.cancel_btn.clicked.connect(MainInterface().clear_and_exit)
        
    def _check_rut(self, rut):
        """Receives a formatted rut"""        
        if not 7 <= len(rut) <= 13:
            return False
            
        last = rut[-1]
        if  not (last in ("k", "K") or last.isdigit()):
            return False
            
        if not "".join(rut[-5:-2]).isnumeric():
            return False
        
        if not "".join(rut[-9:-6]).isnumeric():
            return False
        
        if not "".join(rut[:-10]).isnumeric():
            return False
        
        return True
        
    def _format_rut(self, rut):
        rut = list(rut)
        if not 7 <= len(rut) <= 13:
            return rut
        
        if rut[-2] != "-":
            rut.insert(-1, "-")
            
        if rut[-6] != ".":
            rut.insert(-5, ".")
            
        if rut[-10] != ".":
            rut.insert(-9, ".")
            
        return "".join(rut)
    
    
    def _format_action(self):
        self.rut_entry.setStyleSheet("color: black;")
        current_rut = self.rut_entry.text()
        self.rut_entry.setText(self._format_rut(current_rut))
        
    def _confirm_action(self):
        
        name_map = {self.name_entry : "Nombre", 
                    self.rut_entry : "RUT",
                    self.company_entry : "Empresa"}
        
        blanks = []
        for entry, name in name_map.items():
            if not entry.text():
                blanks.append(name)
                
        if blanks:
            self.error_lbl.setText("Complete: " + ", ".join(blanks))
            return
        
        rut = self.rut_entry.text()
        if not self._check_rut(rut):
            self.rut_entry.setStyleSheet("color: red;")
            self.error_lbl.setText("Ingrese un Rut Válido")
            return
                
        name = self.name_entry.text()
        company = self.company_entry.text()
        
        print(name, rut, company)
        
        MainInterface().set_user_info(name, rut, company)
        MainInterface().change_confirm_register()
        self.error_lbl.setText("") # Clear Errors
        self._reset_fields()
    
    def _reset_fields(self):
        for entry in (self.name_entry, self.rut_entry, self.company_entry):
            entry.setText("")
            entry.setStyleSheet("color: black;")
        

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
        self.confirm_btn.clicked.connect(MainInterface().register_action)
        self.cancel_btn.clicked.connect(MainInterface().clear_and_exit)
        
class RegisterOk(QtWidgets.QWidget):
    def __init__(self):
        super().__init__()
        uic.loadUi('uis/register_ok.ui', self)
        self.ok_btn.clicked.connect(MainInterface().change_main)
        
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
            self.current_card = "test"
            self.current_name = None
            self.current_rut = None
            self.current_company = None
            
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
            self.register_form = RegisterForm()
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

