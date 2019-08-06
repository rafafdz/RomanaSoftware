from base_panel import BasePanel

class UseCard(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/use_card.ui', main_interface)
        self.client_card = True
        self.cancel_btn.clicked.connect(self.main_interface.change_main)
        
    def set_client_card(self):
        self.client_card = True
        self.user_type_lbl = "CLIENTE"
        
    def set_admin_card(self):
        self.self.client_card = False
        self.user_type_lbl = "ADMINISTRADOR"