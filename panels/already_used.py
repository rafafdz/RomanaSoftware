from base_panel import BasePanel

class AlreadyUsed(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/already_used.ui', main_interface)
        self.retry_btn.clicked.connect(self.main_interface.change_card_client)
        self.exit_btn.clicked.connect(self.main_interface.clear_and_exit)