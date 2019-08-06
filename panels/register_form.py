from base_panel import BasePanel
from PyQt5.QtCore import QRegExp
from PyQt5.QtGui import QRegExpValidator

class RegisterForm(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/register_form.ui', main_interface)
        regex_rut = QRegExp("\d{1,3}\.?\d{3}\.?\d{3}\-?[0-9kK]{1}")
        self.rut_entry.setMaxLength(13)
        self.rut_entry.setValidator(QRegExpValidator(regex_rut))
        self.rut_entry.editingFinished.connect(self._format_action)
        self.confirm_btn.clicked.connect(self._confirm_action)
        self.cancel_btn.clicked.connect(self.main_interface.clear_and_exit)
        
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
        
        self.main_interface.set_user_info(name, rut, company)
        self.main_interface.change_confirm_register()
        self.error_lbl.setText("") # Clear Errors
        self._reset_fields()
    
    def _reset_fields(self):
        for entry in (self.name_entry, self.rut_entry, self.company_entry):
            entry.setText("")
            entry.setStyleSheet("color: black;")