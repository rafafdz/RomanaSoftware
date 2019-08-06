from base_panel import BasePanel

class NotRegistered(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/not_registered.ui', main_interface)
