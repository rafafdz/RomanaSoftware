from base_panel import BasePanel

class ChargeOk(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/charge_ok.ui', main_interface)
        self.ok_btn.clicked.connect(self.main_interface.clear_and_exit)
        
    def set_balance(self, balance):
        self.balance_lbl.setText(str(balance))