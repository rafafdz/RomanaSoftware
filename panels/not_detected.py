from base_panel import BasePanel

class NotDetected(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/not_detected.ui', main_interface)
        self.retry_btn.clicked.connect(self.main_interface.change_use_card)
        self.exit_btn.clicked.connect(self.main_interface.change_main)