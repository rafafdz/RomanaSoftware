from base_panel import BasePanel


class RegisterOk(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/register_ok.ui', main_interface)
        self.ok_btn.clicked.connect(self.main_interface.change_main)