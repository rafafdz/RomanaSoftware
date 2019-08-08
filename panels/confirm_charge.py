from base_panel import BasePanel

class ConfirmCharge(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/confirm_charge.ui', main_interface)
        self.confirm_btn.clicked.connect(self.main_interface.change_card_admin)
        self.cancel_btn.clicked.connect(self._log_and_exit)
        
    def set_card(self, card_id):
        self.card_lbl.setText(card_id)
        
    def set_name(self, name):
        self.name_lbl.setText(name)
        
    def set_balance(self, balance):
        self.balance_lbl.setText(str(balance))
        
    def _log_and_exit(self):
        self.main_interface.log_exited_charge()
        self.main_interface.clear_and_exit()