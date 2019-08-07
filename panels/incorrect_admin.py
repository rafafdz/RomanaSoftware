from base_panel import BasePanel

class IncorrectAdmin(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/incorrect_admin.ui', main_interface)
        
        self.retry_btn.clicked.connect(self.main_interface.change_card_admin)
        self.exit_btn.clicked.connect(self.main_interface.clear_and_exit)