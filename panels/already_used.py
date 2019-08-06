from base_panel import BasePanel

class AlreadyUsed(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/already_used.ui', main_interface)