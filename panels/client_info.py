from base_panel import BasePanel

class ClientInfo(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/client_info.ui', main_interface)
        self.exit_btn.clicked.connect(self.main_interface.change_main)