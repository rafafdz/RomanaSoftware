from PyQt5.QtGui import QIntValidator
from base_panel import BasePanel

class ChargeForm(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/charge_form.ui', main_interface)
        self.money_entry.setMaxLength(5)
        self.money_entry.setValidator(QIntValidator())
        self.money_entry.editingFinished.connect(self._check_entry)
        self.charge_btn.clicked.connect(self._charge_action)
        self.cancel_btn.clicked.connect(self._clear_fields_and_exit)
        self.error_lbl.setStyleSheet("color: red;")
        
    def _balance_valid(self, balance):
        return int(balance) == balance and 0 < balance <= 50000
    
    def _charge_action(self):
        balance = float(self.money_entry.text())        
        if self._balance_valid(balance):
            self.main_interface.add_balance_action(balance)
            self._reset_fields()
        
    def _check_entry(self):
        balance = float(self.money_entry.text())
        
        if not int(balance) == balance:
            error = "El monto no puede ser decimal"
        elif balance <= 0:
            error = "El monto tiene que ser positivo"
        elif balance >= 50000:
            error = "No se puede cargar mas de $50.000"
        else:
            error = ""
            
        self.error_lbl.setText(error)
        
    def _reset_fields(self):
        self.money_entry.setText("")
        self.error_lbl.setText("")
        
    def _clear_fields_and_exit(self):
        self._reset_fields()
        self.main_interface.clear_and_exit()
        
    def set_name(self, name):
        self.name_lbl.setText(name)
        
    def set_balance(self, balance):
        self.balance_lbl.setText(str(balance))