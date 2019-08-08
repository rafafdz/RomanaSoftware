from base_panel import BasePanel
from PyQt5.QtCore import QRegExp
from PyQt5.QtGui import QRegExpValidator, QIntValidator

class RegisterForm(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/register_form.ui', main_interface)
        regex_rut = QRegExp("\d{1,3}\.?\d{3}\.?\d{3}\-?[0-9kK]{1}")
        regex_phone = QRegExp("\+569\d{8}")
        
        self.rut_entry.setMaxLength(13)
        self.rut_entry.setValidator(QRegExpValidator(regex_rut))
        self.rut_entry.editingFinished.connect(self._format_action)
        self.confirm_btn.clicked.connect(self._confirm_action)
        self.cancel_btn.clicked.connect(self.main_interface.clear_and_exit)
        self.phone_entry.setValidator(QRegExpValidator(regex_phone))
        self.phone_entry.setMaxLength(12)
        self.phone_entry.selectionChanged.connect(self.phone_entry.deselect)
        
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
                    self.company_entry : "Empresa",
                    self.phone_entry : "Teléfono"}
        
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
        phone = self.phone_entry.text()

        error_msg = None
        if len(phone) != 12:
            error_msg = "El teléfono ingresado es muy corto"
        elif phone[:4] != "+569":
            error_msg = "El teléfono debe empezar con +569"
        if error_msg:
            self.error_lbl.setText(error_msg)
            return
        
        self.main_interface.set_user_info(name, rut, company, int(phone[1:]))
        self.main_interface.change_confirm_register()
        self.error_lbl.setText("") # Clear Errors
        self.reset_form()
    
    def reset_form(self):
        self.phone_entry.setText("+569")
        for entry in (self.name_entry, self.rut_entry, self.company_entry):
            entry.setText("")
            entry.setStyleSheet("color: black;")