from base_panel import BasePanel

class ConfirmRegister(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/confirm_register.ui', main_interface)
        self.confirm_btn.clicked.connect(self.main_interface.change_card_admin)
        self.cancel_btn.clicked.connect(self.main_interface.clear_and_exit)

    def set_card(self, card_id):
        self.card_lbl.setText(card_id)
        
    def set_name(self, name):
        self.name_lbl.setText(name)