from base_panel import BasePanel

class AdminInterface(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/main.ui', main_interface)
        self.register_btn.clicked.connect(self.main_interface.change_register)
        self.charge_btn.clicked.connect(self.main_interface.change_charge)
        self.info_btn.clicked.connect(self.main_interface.change_info)